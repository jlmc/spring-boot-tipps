# Exemplo trabalhado: do mutante sobrevivente ao teste (64% → 80%)

Caso real deste projecto, para mostrar **o raciocínio** (não só o resultado). Complementa [mutation-testing.md](mutation-testing.md).

**Ponto de partida:** `./mvnw -Pmutation verify` → 99 mutantes, 63 mortos (**64%**), line coverage 89%, test strength 69%.
**Chegada:** 79/99 mortos (**80%**), coverage 94%, strength 83%, threshold 60 → 75.

## O raciocínio, passo a passo

### 1. Ler o report por *valor*, não por ordem
Agrupar os sobreviventes por classe (`target/pit-reports/mutations.xml` ou o HTML) e perguntar: "se isto estivesse errado em produção, importava?"

| Classe | Estado | Importa? |
|---|---|---|
| `WebClientProviderEncounterSearchAdapter.jitteredBackoff` | 10 sobreviventes | **Sim** — define quanto esperamos entre retries |
| `RetryAfterHeaderParser` | 7 sobreviventes + 3 sem cobertura | **Sim** — interpreta o que o provider manda; erro = esperar demais/de menos |
| `FixedSizeTimeWindowSplitter` | 3 | Sim (fica para depois) |
| Excepções triviais | sem cobertura | Pouco |

Escolheram-se as duas primeiras: lógica de negócio, pura, e com muitos sobreviventes.

### 2. Perceber *porque* sobrevive
Os testes existentes só confirmavam que **houve retry** (o resultado final), nunca **quanto** se esperou nem **como** se interpretou o header. Um mutante que troca `*` por `/` ou `>=` por `>` não altera o "houve retry", logo passa despercebido.

> Regra: sobrevive o que nenhum teste *observa*. A pergunta certa é "que asserção falta?", não "que teste escrevo?".

### 3. Escolher onde testar (trade-off)
`jitteredBackoff` era `private static` e só alcançável pelo fluxo HTTP completo (lento, aleatório, difícil de afirmar).

| Opção | Prós | Contras |
|---|---|---|
| Testar via adapter com relógio virtual | zero alterações ao código de produção | muito setup; o jitter aleatório dificulta afirmar valores exactos |
| **Tornar `package-private` e testar directamente** (escolhida) | testes pequenos, rápidos, valores exactos | visibilidade ligeiramente maior (comentário no código a explicar) |

Escolheu-se a segunda: custo mínimo, ganho máximo. A alteração é a única em código de produção.

### 4. Desenhar asserções que matem *famílias* de mutantes

**`jitteredBackoff`** — fórmula: `min · 2^(tentativa-1)`, limitada a `max`, com banda de jitter.
- Jitter = 0 → resultado **determinístico**: afirmar valores exactos para tentativas 1..5 e 20 (100, 200, 400, 800, 1000, 1000 ms). Mata trocas de operador (`*`↔`/`, `-`↔`+`) e o tecto.
- Jitter = 0 também mata o mutante de fronteira `jitterRange <= 0` → `<`: com `<`, o código chegaria a `nextDouble(low, high)` com `low == high` e rebentaria com `IllegalArgumentException`.
- Jitter > 0 → **propriedades**, não valores: todos os resultados ficam na banda `[base−range, base+range]` (500 amostras), há variação real e usa os dois lados do valor base.
- Jitter aplicado ao valor **já limitado** (tentativa 10 → banda à volta de 1000 ms, não de 51 200 ms).
- Jitter = 1.0 → nunca negativo (mata a remoção do `Math.max(0, …)`).

**`RetryAfterHeaderParser`** — um caso por ramo e por fronteira:
- `null`/vazio/em branco → vazio; `"0"` → `Duration.ZERO` (fronteira `>= 0`, o caso que mais sobrevive); `"120"`, `"  30 "` (trim); `"-1"` → vazio; lixo (`"abc"`, `"1.5"`, data inválida) → vazio.
- Data HTTP no futuro → tempo restante (afirmado num intervalo, porque o relógio avança); no passado → `ZERO`; noutro fuso → mesmo resultado.

> Regra: para lógica numérica determinística, **valores exactos**; para aleatória, **invariantes** (limites, variação).

### 4.1 Evitar testes frágeis
- Datas relativas a `now()` → asserções por **intervalo** (`isBetween(100s, 120s)`), nunca igualdade.
- Aleatoriedade → muitas amostras + limites, nunca uma só execução.

### 5. Medir e interpretar o que sobra
Voltou-se a correr o PIT: 64% → **80%**. Restou 1 sobrevivente em `RetryAfterHeaderParser` (linha do `isBlank`).

**É equivalente?** Sem o `isBlank`, `"   "` seguiria para `trim()` → `""` → `parseLong` falha → parse de data falha → `Optional.empty()`. O resultado observável é idêntico, logo **nenhum teste o pode matar**. Decisão: documentar e ignorar. Perseguir 100% seria esforço sem valor.

### 6. Fixar o threshold
Regra: threshold = score medido − ~5 pontos (80 → **75**). Margem para variação legítima; não permite regressões grandes; sobe aos poucos. Verificado: `./mvnw -Pmutation verify` passa com 75 e falha acima do score.

### 7. Documentar e commitar por contexto
Um commit por contexto (teste do parser; teste do backoff + visibilidade; threshold + docs). Mensagens no formato convencional (`test(provider): …`, `chore(mutation): …`).

## Checklist reutilizável
1. Agrupar sobreviventes por classe; priorizar lógica de negócio.
2. Para cada família: que comportamento **não é observado**?
3. Decidir onde testar (fluxo completo vs unidade) e o custo em código de produção.
4. Valores exactos se determinístico; invariantes se aleatório; intervalos se depende do relógio.
5. Voltar a medir; classificar o que sobra (equivalente, trivial, por fazer).
6. Fixar threshold = score − ~5 e registar a baseline.
7. Escrever o que aprendeste (este documento).
