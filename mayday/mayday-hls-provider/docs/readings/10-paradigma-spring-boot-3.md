# Mutation testing em projectos Spring Boot 3: vencer a lentidão (Ismail Ahmedov, Paradigma, 23-09-2025)

- **Fonte:** https://en.paradigmadigital.com/dev/mutation-testing-spring-boot-3-projects-beat-slowness-supercharge-tests/
- **Tipo:** caso prático com Maven
- **Leitura:** artigo completo lido; as capturas de ecrã com estatísticas não são texto, por isso os valores vêm só do corpo.

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
