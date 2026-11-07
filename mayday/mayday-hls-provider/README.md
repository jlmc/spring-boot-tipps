# mayday-hls-provider

POC arquitectural: pesquisa concorrente a um provider externo, dividida em janelas temporais,
implementada como referência de **Hexagonal Architecture / Ports and Adapters** com
**Java 25 + Spring Boot 4 + Spring WebFlux + Project Reactor**, 100% reactive, sem `block()`.

## Objectivo

Provar uma arquitectura tecnicamente sólida — não apenas código funcional — para o seguinte
problema: um cliente pede `GET /encounters` com um intervalo temporal; o intervalo é dividido
em janelas (`window-size` configurável); cada janela gera uma chamada HTTP independente a um
provider externo; as chamadas correm concorrentemente com um limite máximo **por account**;
a pesquisa termina assim que qualquer janela encontrar o recurso, cancelando de imediato
(via Reactor nativo) as janelas ainda pendentes, os seus retries e delays.

## Arquitectura

```
                    +-----------------------+
                    |   HTTP Adapter (in)   |   adapters/in/web
                    |  EncounterController  |
                    +-----------+-----------+
                                |
                                v
                    +-----------------------+
                    |   Application Core    |   application/  (zero Spring/HTTP)
                    |  SearchEncountersUseCase (port in)
                    |  SearchEncountersService
                    |  TimeWindowSplitter        }  domain/
                    +-----------+-----------+
                                |
                         Ports (out)
                                |
              +-----------------+-----------------+
              |                                   |
              v                                   v
   ProviderEncounterSearchPort          AccountConcurrencyPolicy
              |                                   |
              v                                   v
   WebClientProviderEncounterSearchAdapter   ConfigAccountConcurrencyPolicy
   (adapters/out/provider)                   (adapters/out/configuration)
```

**Regra fundamental**: `adapters` e `configuration` dependem de `application`/`domain`;
nunca o inverso. `domain/` e `application/` não têm uma única importação de Spring, WebFlux,
WebClient ou HTTP.

### Packages

| Package | Conteúdo |
|---|---|
| `domain` | `AccountId`, `TimeWindow`, `Encounter`, `EncounterResult` (records), `TimeWindowSplitter` + `FixedSizeTimeWindowSplitter` |
| `application` | `SearchEncountersService`, excepções da aplicação |
| `application.port.in` | `SearchEncountersUseCase`, `AccountResolver` |
| `application.port.out` | `ProviderEncounterSearchPort`, `AccountConcurrencyPolicy` |
| `adapters.in.web` | `EncounterController`, `EncounterResponse`, `BearerAccountResolver`, `GlobalExceptionHandler` |
| `adapters.out.provider` | `WebClientProviderEncounterSearchAdapter`, `ProviderCallOutcome` (sealed), DTOs, `RetryAfterHeaderParser` |
| `adapters.out.configuration` | `ConfigAccountConcurrencyPolicy` |
| `configuration` | `@ConfigurationProperties` records, wiring dos beans (`WebClient`, use case) |

## Domain

Records imutáveis, sem framework:

```java
record AccountId(String value)
record TimeWindow(Instant start, Instant end)
record Encounter(String id, String visitType, Instant date, long durationSeconds)
record EncounterResult(String encounterId, Instant encounterDate, long durationSeconds)
```

## Inbound Port

```java
interface SearchEncountersUseCase {
    Mono<EncounterResult> search(AccountId accountId, Instant startDate, Instant endDate, String visitType);
}
```

O `EncounterController` limita-se a: extrair a account do header, chamar o use case, mapear
o resultado para a resposta HTTP. Nenhuma lógica de negócio no controller.

## Outbound Ports

```java
interface ProviderEncounterSearchPort {
    Flux<Encounter> search(AccountId accountId, TimeWindow window, String visitType);
}
interface AccountConcurrencyPolicy {
    int maxConcurrency(AccountId accountId);
}
```

O Application Core depende só destas interfaces — nunca conhece WebClient, HTTP, YAML.

## Time Windows

`FixedSizeTimeWindowSplitter.split(start, end, windowSize)` usa semântica `[start, end)`.
Casos tratados (com testes dedicados):

- intervalo menor que a window → uma única janela parcial
- divisão exacta → N janelas completas
- última janela parcial → clampada a `end`
- `start >= end` → lista vazia (decisão: "nada para pesquisar", em vez de excepção —
  compõe directamente com o use case sem caso especial)

## Concorrência por account (não é rate limiting)

```java
int maxConcurrency = accountConcurrencyPolicy.maxConcurrency(accountId);
Flux.fromIterable(windows)
    .flatMap(window -> searchWindow(accountId, window, visitType), maxConcurrency)
    .next();
```

`maxConcurrency` é resolvido **dinamicamente por account** através da Port
`AccountConcurrencyPolicy`, nunca hardcoded. Configuração (`ConfigAccountConcurrencyPolicy`):

```yaml
mayday:
  accounts:
    concurrency:
      default: 5
      account-123: 3
```

Falha rápido no arranque (não silenciosamente numa request) se `default` estiver em falta
ou algum valor for `<= 0` (validado no compact constructor de `AccountsProperties`).

**Distinção importante — concurrency ≠ rate limiting:**

- `maxConcurrency` limita quantos pedidos **em voo** esta pesquisa, neste pod, faz
  simultaneamente para uma account. Não é "N pedidos por segundo".
- Não é um limite global: várias pesquisas simultâneas da mesma account (ex.: 3 pesquisas
  com `maxConcurrency=5`) podem gerar até 15 pedidos concorrentes — a policy não sabe disso,
  nem precisa de saber.
- Com N pods, cada pod aplica a sua própria configuração local; o agregado real contra o
  provider pode chegar a `N × maxConcurrency`. Não há coordenação entre pods nesta POC.
- Quem realmente impõe o limite é o **provider**, via HTTP 429. É isso que tratamos
  correctamente (ver secção seguinte) — não fingimos ter um rate limiter global quando não
  temos.
- Se no futuro for necessário um limite global real (`account-A ≤ X req/s` entre todos os
  pods e pesquisas), isso exige uma solução distribuída (ex. Redis token bucket) — fora do
  âmbito desta POC.

## Cancelamento

`.next()` implementa "o primeiro resultado a chegar ganha" — **não** o cronologicamente mais
antigo. Assim que qualquer janela emite um match, `.next()` cancela nativamente a subscrição
Reactor do `flatMap`, que por sua vez cancela todas as janelas ainda activas (incluindo
pedidos HTTP em curso, delays de retry pendentes) e nunca subscreve janelas restantes. Sem
`AtomicBoolean`, sem `Future.cancel`, sem bookkeeping manual — é o comportamento nativo do
Reactor quando um `Retry.from` custom compõe delays com `Mono.delay` na mesma cadeia que é
cancelada.

**Importante:** cancelamento Reactor (client-side) **não garante** cancelamento no servidor
remoto. Se o provider já recebeu o pedido HTTP, pode continuar a processá-lo — controlamos
apenas a subscrição deste lado.

## Rate limiting: 429 / Retry-After

O provider é a autoridade sobre rate limiting. Quando devolve `429`:

1. É detectado explicitamente (`ProviderCallOutcome.RateLimited`, via `switch` exhaustivo
   sobre um sealed interface);
2. O header `Retry-After` é parseado (`RetryAfterHeaderParser`, suporta delay-seconds e
   HTTP-date);
3. O retry espera esse tempo (reactivamente, `Mono.delay`, nunca `Thread.sleep`);
4. Se `Retry-After` estiver ausente, usa-se backoff exponencial + jitter configurável;
5. Tentativas limitadas por `mayday.provider.retry.max-attempts` (nunca infinito);
6. 5xx: mesmo backoff+jitter, tentativas limitadas;
7. Outros 4xx: **sem retry**, propagado de imediato.

### Porque Reactor nativo (`Retry.from`) e não Resilience4j Retry

O atraso do 429 é **dinâmico por tentativa** — vem da própria resposta (`Retry-After`), não
de uma função estática tentativa→atraso. O `Resilience4j Retry` assenta num `IntervalFunction`
que não recebe naturalmente um valor vindo de uma excepção específica sem um canal lateral
mutável (exactamente o bookkeeping manual que se quer evitar, e sem afinidade de thread
garantida num pipeline reactive). `Retry.from(Flux<RetrySignal> -> Publisher<?>)` recebe a
excepção real em `signal.failure()`, pelo que o atraso sai directamente dela — mais simples,
mais transparente, e o cancelamento é automático por ser só mais um operador Reactor na mesma
cadeia. `spring-cloud-starter-circuitbreaker-reactor-resilience4j` continua no `pom.xml`
como dependência disponível — avaliada, não usada nesta feature (ver "Evolução futura").

### Porque não Resilience4j RateLimiter

Um `RateLimiter` do Resilience4j é local à JVM/processo. Com múltiplos pods, cada um teria o
seu próprio contador não sincronizado — uma falsa sensação de limite global. Esta POC não
implementa nenhum rate limiter proactivo; a autoridade é o provider (via 429), e é a resposta
a essa autoridade que implementamos correctamente.

## JDK 25

- **Records** para todo o domínio, DTOs e `@ConfigurationProperties` (incluindo records
  aninhados, suportados nativamente por constructor binding).
- **Sealed interface + pattern matching exhaustivo em `switch`**: `ProviderCallOutcome`
  (`Success` / `RateLimited` / `ServerError` / `ClientError`) é o único ponto de tradução
  HTTP→domínio/erro no adapter — o compilador garante que nenhum caso é esquecido.
- **Unnamed variables** (`catch (NumberFormatException _)`) onde o valor da excepção não é
  usado.
- **Não usadas**: *value classes* e *structured concurrency* continuam em preview no JDK 25
  (confirmado — structured concurrency está na 5ª preview, JEP 505); evitar preview features
  numa referência arquitectural mantém o build simples (sem `--enable-preview`).

## Porque não Virtual Threads

O fluxo principal (`HTTP → use case → WebClient → provider`) é inteiramente reactive
(WebFlux + Reactor + WebClient), sem nenhuma chamada bloqueante a esconder atrás de uma
virtual thread. Substituir Reactor por Virtual Threads só faria sentido se houvesse trabalho
bloqueante genuíno (ex. JDBC síncrono, ficheiros) — não é o caso aqui. Decisão: **não usar
Virtual Threads** neste POC; documentada e não uma omissão.

## Configuração

```yaml
server:
  port: 8080

mayday:
  provider:
    base-url: http://localhost:9090
    search:
      window-size: 1d
    http:
      connect-timeout: 2s
      response-timeout: 5s
    retry:
      max-attempts: 4
      backoff:
        min-backoff: 200ms
        max-backoff: 5s
        jitter-factor: 0.5
  accounts:
    concurrency:
      default: 5
      account-123: 3
      account-A: 4
      account-B: 2
```

`@ConfigurationProperties` records tipados (`ProviderProperties`, `AccountsProperties`),
sem `@Value` espalhado pelo código.

## WireMock e demo

- **Testes automatizados** (`WireMockExtension`, JUnit 5, `org.wiremock:wiremock-standalone`):
  stubs programáticos por teste — cobertura de query params, header `X-Account-Id`,
  parsing de match/vazio, cancelamento de janela lenta, 429+Retry-After, 429 repetido
  (esgota tentativas), 5xx com backoff+jitter, outro 4xx sem retry, rate-limit independente
  por account.
- **Demo manual**: `docker-compose.yaml` na **raiz do repositório** (fora deste módulo, por
  decisão explícita) sobe um WireMock (`wiremock/wiremock:3.13.2`) na porta `9090`, com
  mappings declarativos em `../docker/wiremock/mappings/`:
  - janela `2026-09-05` → match determinístico (`encounter-123`);
  - janela `2026-09-03` → lenta (`fixedDelay: 5000ms`), para demonstrar cancelamento;
  - qualquer outra janela → vazio (catch-all, prioridade baixa);
  - `account-A` / `account-B` → rate-limit independente via **Scenarios nativos do
    WireMock** (state machine por `scenarioName`) — 2 pedidos OK, depois `429` persistente
    com `Retry-After: 1`. Sem extensão Java custom, sem Redis: o estado de scenario é isolado
    por nome, o que já garante independência entre accounts.

  > **Nota**: como `spring-boot-docker-compose` está no classpath mas o compose vive fora do
  > working directory do módulo, a auto-gestão do Boot foi explicitamente desligada
  > (`spring.docker.compose.enabled: false`) — caso contrário a aplicação falha o arranque
  > (`IllegalStateException: No Docker Compose file found`), em vez de simplesmente ignorar.
  > Isto foi confirmado ao correr a aplicação, não assumido.

### Como correr

```bash
# a partir da raiz do repositório
docker compose up -d

# noutro terminal, dentro do módulo
cd mayday-hls-provider
./mvnw spring-boot:run
```

```bash
curl -H "Authorization: Bearer account-123" \
  "http://localhost:8080/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-10T00:00:00Z&visitType=FOLLOW_UP"
```

Resposta esperada (nunca 404/vazio — sempre `200` com uma flag `found`):

```json
{"found":true,"encounterId":"encounter-123","encounterDate":"2026-09-05T10:00:00Z","durationSeconds":1800}
```

Um script de demonstração completo (`test-api.sh`, na raiz do repositório) exercita match,
sem-match, 401, 400, rate-limit por account e o 502 de retries esgotados — correr com
`./test-api.sh` depois dos dois passos acima.

## Testes

```bash
cd mayday-hls-provider
./mvnw test
```

37 testes, sem rede externa nem docker necessário (WireMock em processo via
`WireMockExtension`):

| Classe | Cobre |
|---|---|
| `FixedSizeTimeWindowSplitterTest` | os 5 casos de divisão de janelas |
| `ConfigAccountConcurrencyPolicyTest` | account conhecida/unknown/default, fail-fast em config inválida |
| `SearchEncountersServiceTest` | corrida de janelas, "quem chega primeiro ganha", cancelamento das restantes, concorrência máxima por account (com um port fake — sem Spring/WireMock) |
| `WebClientProviderEncounterSearchAdapterTest` | headers/query params, parsing, 429+Retry-After, 429 esgotado, 5xx, 4xx sem retry, rate-limit independente por account (WireMock real, in-process) |
| `BearerAccountResolverTest` | header válido/ausente/malformado |
| `EncounterControllerTest` | 401/400/200 (`found=true`/`found=false`)/502 |
| `EncountersEndToEndTest` | fluxo completo, contexto Spring real + WireMock |

### Mutation testing (PIT)

Mede a qualidade das asserções, não só a cobertura. Fora do build por omissão:

```bash
./mvnw -Pmutation verify    # relatório em target/pit-reports/index.html; threshold em pitest.mutation-threshold
```

Configuração Maven, Gradle (Kotlin DSL) e multi-módulo: [docs/mutation-testing.md](docs/mutation-testing.md).

## Decisões e trade-offs assumidos

- **API nunca devolve 404 nem corpo vazio** para "sem match" — devolve sempre `200` com
  `EncounterResponse.found: boolean` (pedido explícito). Erros genuínos (401/400/502)
  mantêm-se como erros HTTP reais.
- `AccountResolver` fica em `application.port.in` (é usado pelo lado inbound para interpretar
  o header, não é uma chamada a um sistema externo).
- `SearchEncountersService` é instanciado via `@Bean` em `DomainBeansConfiguration`, não
  auto-anotado com `@Service` — mantém `application/` sem uma única anotação Spring.
- Um 4xx do provider que não seja 429 é mapeado para `502` no controller (é uma falha
  upstream do ponto de vista de quem chama esta API, não um erro do próprio pedido).
- Módulo Testcontainers oficial do WireMock existe mas está em alpha
  (`1.0-alpha-13`+) — não usado por risco de estabilidade numa referência arquitectural;
  preferido o `WireMockExtension` oficial, estável.

## Evolução futura (não implementado nesta POC)

- `AccountConcurrencyPolicy`: `DatabaseAccountConcurrencyPolicy`, `RemoteAccountConcurrencyPolicy`,
  `FeatureFlagAccountConcurrencyPolicy` — o use case não precisa de mudar, só o adapter.
- Rate limiting distribuído real (ex. Redis token bucket) se for necessário um limite
  `account → X req/s` coordenado entre pods.
- `CircuitBreaker`/`Bulkhead` via `spring-cloud-starter-circuitbreaker-reactor-resilience4j`
  (já presente no `pom.xml`, não usado nesta feature).
- Provider real em vez de WireMock; OAuth/JWT real em vez do `Bearer account-123` literal;
  observability/metrics (Micrometer, já disponível via Actuator).

## Revisão arquitectural

1. **Application Core**: `domain/` + `application/` — zero imports de Spring/WebFlux/HTTP.
2. **Inbound Ports**: `SearchEncountersUseCase`, `AccountResolver`.
3. **Outbound Ports**: `ProviderEncounterSearchPort`, `AccountConcurrencyPolicy`.
4. **Adapters**: `EncounterController`/`BearerAccountResolver` (in, web),
   `WebClientProviderEncounterSearchAdapter` (out, provider),
   `ConfigAccountConcurrencyPolicy` (out, configuration).
5. **O core depende de Spring?** Não — `domain/` e `application/` não têm uma única
   anotação ou import de Spring/WebFlux/WebClient.
6. **Concorrência por account**: resolvida dinamicamente via `AccountConcurrencyPolicy`,
   passada ao `flatMap(..., maxConcurrency)`.
7. **Trocar configuration por DB**: substituir `ConfigAccountConcurrencyPolicy` por uma
   nova implementação da mesma Port; `SearchEncountersService` não muda uma linha.
8. **Cancelamento**: `.next()` cancela nativamente o `flatMap` (incluindo retries/delays
   pendentes) assim que o primeiro match chega — sem código de cancelamento manual.
9. **429 do provider**: detectado via `ProviderCallOutcome.RateLimited`, tratado como
   retry reactivo bounded, nunca como erro fatal imediato.
10. **Retry-After**: parseado (`RetryAfterHeaderParser`), usado como delay exacto do retry
    quando presente; fallback para backoff exponencial+jitter quando ausente.
11. **N pods**: cada pod aplica `maxConcurrency` independentemente; o agregado real pode
    chegar a `N × maxConcurrency` — sem coordenação entre pods nesta POC.
12. **Sem distributed rate limiter**: um limiter local (Resilience4j) daria uma falsa
    sensação de limite global; a solução real exigiria estado partilhado (Redis) — fora
    do âmbito de uma POC.
13. **Concurrency vs rate limiting**: `maxConcurrency` é "quantos pedidos em voo agora";
    rate limiting é "quantos pedidos por segundo" — são eixos diferentes, e só o segundo é
    imposto (pelo provider, via 429).
14. **Várias pesquisas simultâneas da mesma account**: cada pesquisa aplica o seu próprio
    `maxConcurrency` de forma independente; não há um limite agregado entre pesquisas.
15. **Porquê WebFlux/Reactor em vez de Virtual Threads**: não há bloqueio genuíno a esconder;
    o cancelamento composicional (retries, delays, chamadas HTTP) é uma propriedade nativa
    do modelo reactive, não de threads.
16. **Features JDK 25 usadas**: records (domínio, config), sealed interface + pattern
    matching exhaustivo (`ProviderCallOutcome`), unnamed variables.
17. **O que falta para production-ready**: provider real; OAuth/JWT real; rate limiting
    distribuído se necessário; CircuitBreaker/Bulkhead activos; métricas/tracing
    (Micrometer/OpenTelemetry); testes de carga; `DatabaseAccountConcurrencyPolicy` com
    cache; hardening de segurança (validação de input mais estrita, rate limiting no
    próprio inbound adapter).
