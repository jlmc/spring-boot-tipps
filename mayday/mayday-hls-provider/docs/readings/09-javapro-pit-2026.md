# Test Your Tests: Mutation Testing in Java with PIT (Julius Mischok, JAVAPRO, 21-01-2026)

- **Fonte:** https://javapro.io/2026/01/21/test-your-tests-mutation-testing-in-java-with-pit/
- **Tipo:** artigo prático, curto
- **Leitura:** artigo completo lido (as imagens dos relatórios não).

## Em resumo
**Tópico:** PIT na prática.
- `>` por `>=` mantém 100% de cobertura mas ninguém testa a fronteira.
- Precisa do `pitest-junit5-plugin` e do `test-compile` antes do `pitest`.
- Três métricas: line coverage, mutation coverage e test strength.
- As metas 60–80% e >90% não têm fonte; propõe partir o build e também correr só de noite, o que se contradiz.

## Tese
Cobertura mede **quantidade**, não qualidade. Mutation testing com PIT pergunta se os testes apanhariam um bug introduzido de propósito.

## Pontos de maior interesse
- **Exemplo-chave:** trocar `>` por `>=` num filtro mantém 100% de cobertura de linha e de ramo, mas nenhum teste cobre a **fronteira**. O método `findPersonsOlderThan` é testado com limites 57 e 5; nenhum cai na fronteira e o mutante sobrevive.
- **Maven:** `org.pitest:pitest-maven` (o artigo usa 1.15.5) com `pitest-junit5-plugin` (1.2.1), necessário com JUnit 5.
- Execução: `./mvnw test-compile pitest:mutationCoverage`; o `test-compile` é obrigatório antes. Relatório em `target/pit-reports/`.
- **Mutadores citados:** Conditional Boundary, Increments, Invert Negatives, Math, Void Method Call, Empty Returns.
- **Três métricas:** line coverage; **mutation coverage** (mortos / todos); **test strength** (mortos / mutantes em código coberto). São as mesmas que usámos no nosso report.
- **Performance:** `withHistory` (incremental por hashes), limitar classes/pacotes, excluir testes lentos, correr de noite ou semanalmente. Exemplo ilustrativo: 30 s de suite podem virar ~30 min.
- **Armadilhas:** testes instáveis distorcem; mutantes equivalentes (`a+0` vs `a-0`) nunca morrem; olhar para **clusters** de sobreviventes, não casos isolados.
- **CI:** jobs dedicados ou agendados, fora do pipeline de entrega; guardar relatórios ao longo do tempo.

## Limites
Não mostra o bloco `<configuration>`, nem exemplo de threshold, nem Gradle; os snippets têm gralhas; as metas (**60–80%** de mutation coverage, **>90%** de test strength) **não têm fonte**; nada específico de Spring.

## Para a nossa equipa
- Útil: dependência JUnit 5, `test-compile` antes, `withHistory`, e correr o PIT fora do pipeline principal.
- **Contradição interna a notar:** propõe "partir o build" abaixo de um threshold e, ao mesmo tempo, correr só de noite. Tratar metas numéricas como tendência, não como alvo (risco de optimizar a métrica). O nosso threshold 75 é um piso de regressão, não um objectivo.

## Aprofundamento

**O exemplo `findPersonsOlderThan`, passo a passo.**
- *Artigo:* o método filtra pessoas com `age > olderThan`; a versão refactorizada com stream usa `.filter(p -> p.age >= olderThan)`, e a troca de `>` por `>=` é o bug. Dez pessoas com idades 15, 15, 36, 56, 10, 60, 19, 19, 36, 2. Testes: limite 57 espera só Jamal (60); limite 5 espera 9 resultados.
- *Inferência nossa:* o mutante sobrevive porque nenhuma chamada usa um limite igual a uma idade existente. Com 57, `>= 57` e `> 57` dão só o de 60; com 5, ninguém tem 5 anos, logo `>= 5` dá os mesmos 9.
- *Teste que o mata (nosso):* chamar com limite **36**. O original devolve 2 pessoas (56 e 60); o mutante devolve 4 (acrescenta as duas de 36). Uma asserção sobre o tamanho (2) ou `containsExactly(...)` mata-o.

**Mutadores citados**, com exemplos **nossos** (o artigo só define a troca):

| Mutador | Exemplo |
|---|---|
| Conditional Boundary | `if (a > b)` → `if (a >= b)` |
| Increments | `i++` → `i--` num ciclo |
| Invert Negatives | `int limite = -5;` → `5` |
| Math | `total = preco * qtd` → `preco / qtd` |
| Void Method Call | `repo.save(entity);` é removido |
| Empty Returns | `return lista;` → `Collections.emptyList()`; `String` → `null`; `int` → `0` |

**As três métricas, com números inventados por nós para ilustrar.** 100 mutações possíveis, 20 em linhas que nenhum teste executa, 80 em código coberto, 60 mortas. *Mutation coverage* = 60 / 100 = 60%. *Test strength* = 60 / 80 = 75%. Ambiguidade do artigo: diz que o PIT só aplica mutações a código coberto, mas não esclarece se o denominador da mutation coverage inclui as de código não coberto.

**`withHistory`.** *Artigo:* guarda *hashes* das classes de teste e de produção numa pasta e, na execução seguinte, salta o que não mudou; não indica a pasta nem a sintaxe. *Inferência:* a granularidade por classe e a reutilização de resultados anteriores não estão confirmadas no texto.

**Configuração mostrada.** Só: `org.pitest:pitest-maven` 1.15.5 com a dependência `pitest-junit5-plugin` 1.2.1, e o comando `./mvnw test-compile pitest:mutationCoverage`. A dependência junta o suporte a JUnit 5 ao classpath do PIT (sem ela, segundo o artigo, não detecta testes); o `test-compile` é obrigatório. **Não mostra:** `targetClasses`, `targetTests`, thresholds, Gradle (só um link para o plugin).

**Por ler:** as imagens dos relatórios, o link do plugin Gradle, `pitest.org` e o guia *Getting Started*.

**No nosso projecto.** O nosso `FixedSizeTimeWindowSplitter` tem 3 sobreviventes de fronteira do mesmo tipo (`<` contra `<=` nos limites das janelas): é o próximo candidato para um teste "com valor na fronteira", como o limite 36 acima.
