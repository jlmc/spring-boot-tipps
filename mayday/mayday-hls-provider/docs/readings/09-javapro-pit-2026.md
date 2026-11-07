# Test Your Tests: Mutation Testing in Java with PIT (Julius Mischok, JAVAPRO, 21-01-2026)

- **Fonte:** https://javapro.io/2026/01/21/test-your-tests-mutation-testing-in-java-with-pit/
- **Tipo:** artigo prático, curto
- **Leitura:** artigo completo lido (as imagens dos relatórios não).

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
