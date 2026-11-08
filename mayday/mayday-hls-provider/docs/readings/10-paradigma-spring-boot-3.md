# Mutation testing em projectos Spring Boot 3: vencer a lentidão (Ismail Ahmedov, Paradigma, 23-09-2025)

- **Fonte:** https://en.paradigmadigital.com/dev/mutation-testing-spring-boot-3-projects-beat-slowness-supercharge-tests/
- **Tipo:** caso prático com Maven
- **Leitura:** artigo completo lido; as capturas de ecrã com estatísticas não são texto, por isso os valores vêm só do corpo.

## Em resumo
**Tópico:** vencer a lentidão em Spring Boot.
- Só testes unitários; excluir integração e código gerado (OpenAPI, MapStruct).
- Mortos: 2% → 16% (excluir gerado) → 37% (melhores asserções).
- Validar o corpo da resposta, e não só o status, matou os sobreviventes.
- Descartes reduz os mutantes (19 → 10 no exemplo), mas dá menos sinal sobre a lógica interna.

## Tese
Mutation testing é lento em projectos reais, mas fica viável se se limitar a **testes unitários**, se o PIT for bem configurado e se se usar o motor **Descartes** (menos mutantes).

## Pontos de maior interesse
- **Regras:** testes deterministas, muito rápidos, independentes da ordem e paralelizáveis. Excluir integração, E2E, performance, contrato e testes que alteram estado global.
- **Porque é lento:** o PIT corre todos os testes por cada mutante. Um único teste com Testcontainers fez o primeiro run falhar.
- **Exclusões:** `excludedTestClasses` para `*IntegrationTest`; `excludedClasses` para `*Application`, DTOs, `*MapperImpl` e utilitários gerados (OpenAPI, MapStruct), que dão mutantes ruidosos.
- **Optimizações:** `timestampedReports=false`, `threads=8`, `withHistory=true` (reutiliza dados em execuções locais seguintes).
- **Threshold:** `mutationThreshold=80` parte o build abaixo disso.
- **O que realmente matou mutantes:** os testes só validavam o status HTTP; passar a validar o **corpo JSON** e o erro 404 em `problem+json` matou os sobreviventes.
- **Descartes:** `mutationEngine=descartes`, modo "extreme" (remove a lógica dos métodos): menos mutantes.
- Versões finais do artigo: pitest-maven 1.20.2, pitest-junit5-plugin 1.2.3, descartes 1.3.3. Correr `mvn clean test` antes do `pitest:mutationCoverage`.

## Números
Mutantes mortos: 2% depois de excluir integração → 16% depois de excluir código gerado → 37% depois de corrigir asserções. Controlador de erros: 19 mutantes, todos mortos; com Descartes, 10 (todos mortos). Benchmark **de terceiros** (Apache Flink, do README do Descartes): 43 619 mutantes em 2 h 29 m (Gregor) contra 4935 em 14 min (Descartes).

## Limites
Exemplo minúsculo (10–19 mutantes), sem tempos absolutos próprios; o threshold de 80% não é justificado; excluir integração deixa fora comportamento real; o modo "extreme" dá menos sinal sobre lógica interna; o Descartes esteve quatro anos sem release; só Maven em detalhe; **nada de WebFlux** (os exemplos usam `@WebMvcTest`).

## Para a nossa equipa
- É o paralelo mais próximo do nosso caso: excluir `*ApplicationTests`/`*EndToEndTest` e código gerado foi exactamente o que fizemos.
- A lição de fundo coincide com a nossa: o score só sobe quando as **asserções** melhoram (corpo da resposta, não só o status).
- Sugestões nossas: usar `withHistory` localmente; testar o Descartes num módulo antes de adoptar; para WebFlux, `WebTestClient`/`StepVerifier` podem ser unitários mas há que validar com o PIT.

## Aprofundamento

**A sequência de etapas.**

| # | Passo | Resultado |
|---|---|---|
| 1 | Estado inicial: pitest-maven 1.7.6 com junit5-plugin 0.16, sem configuração | **falha**: um teste de integração com Testcontainers é lento e o PIT corre todos os testes antes dos mutantes |
| 2 | `excludedTestClasses` para `*IntegrationTest` | corre até ao fim; 2% mortos |
| 3 | `excludedClasses` para código gerado | menos tempo; 16% mortos |
| 4 | Optimizações e `mutationThreshold=80` | o build falha por estar abaixo de 80%; reexecução com histórico mais rápida |
| 5 | Asserções reforçadas nos controladores | 37% mortos; todos os mutantes do pacote de controladores mortos |
| 6 | Teste novo para o `GlobalExceptionHandler` | 19 mutantes, todos mortos |
| 7 | `mutationEngine=descartes` | 10 mutantes em vez de 19, todos mortos |
| 8 | Actualizar versões (pitest-maven 1.20.2, junit5-plugin 1.2.3, descartes 1.3.3) | mesmo resultado; o relatório passa a mostrar testes lentos (limiar 2 s; o mais lento 109 ms) |

*Inferência nossa:* a subida de 2% para 16% vem sobretudo de **retirar mutantes ruidosos do total**, não de testes novos.

**O teste do controlador de erros.**
- *Artigo, antes:* o `GlobalExceptionHandler` era a única classe com 6 mutantes sobreviventes.
- *Artigo, depois:* um teste novo com `@WebMvcTest` (incluindo o controlador e o handler) faz GET a `/products/100` com a excepção "produto não encontrado" e verifica **status 404, content type `application/problem+json` e os campos title, detail e type**. Repete para o carrinho inexistente.
- *Inferência nossa:* o artigo não mostra o teste anterior; o caminho de erro provavelmente não era exercitado ou não se verificava o corpo. O nome do teste (`return_basket_not_found`) num ficheiro de produtos parece um lapso de cópia.

**Opções do pom (cada uma).** `mutationEngine=descartes` (troca o motor Gregor); `timestampedReports=false` (relatório numa pasta fixa); `threads=8` (análise em paralelo; o artigo diz "por exemplo", não é recomendação); `withHistory=true` (reutiliza dados de execuções anteriores); `mutationThreshold=80` (parte o build abaixo disso); `excludedClasses`/`excludedTestClasses`. Ordem: `mvn clean test` antes de `mvn pitest:mutationCoverage`, porque o PIT lê código compilado.

**Descartes e o modo "extreme".** *Artigo:* remove a lógica do método testado; num método `void` retira todas as instruções, nos restantes o corpo passa a um único `return`. *Exemplo nosso:* num `int precoComIva(int base)`, o mutante "extreme" devolve só um valor por omissão: um teste que só verifica que não há excepção deixa-o vivo, um que verifica o valor calculado mata-o. Num `registar(x)` que chama `repo.save(x)`, só mata o mutante um teste que verifique essa chamada.

**Benchmark do Apache Flink core (de terceiros, do README do Descartes).** Motor padrão: 43 619 mutantes em 2 h 29 m 45 s. Descartes: 4935 mutantes em 14 m 04 s. *Cálculo nosso:* ~88,7% menos mutantes e ~10,6 vezes menos tempo (8985 s contra 844 s). O artigo não diz se o hardware e as threads foram iguais.

**Limite:** os números finais do exemplo (10 mutantes) são pequenos demais para tirar conclusões de desempenho.

**No nosso projecto.** A sequência é uma boa checklist, e a nossa ordem foi igual: exclusões → threshold → testes. O que o Paradigma resolveu validando o **corpo** da resposta, resolvemos nós afirmando **valores exactos** no backoff e no parser.

**Por ler:** as capturas de ecrã, o repositório do exemplo e o `performance-comparison.md` do Descartes.
