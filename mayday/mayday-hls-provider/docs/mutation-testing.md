# Mutation testing com PIT (Maven, Gradle Kotlin DSL, multi-módulo)

Guia de configuração do [PIT (Pitest)](https://pitest.org) para projectos Spring/WebFlux, em Maven e em Gradle (Kotlin DSL), desde um projecto simples (este) até um monorepo multi-módulo (caso de estudo: `hls-providers`).

> **Estado de validação.** Marcado como ✅ o que foi executado de facto; ⚠️ o que é recomendação não executada.
> - ✅ Maven neste projecto: Java 25, PIT 1.30.2, `pitest-junit5-plugin` 1.2.3.
> - ✅ Gradle 8.10.2 + JDK 21 + Kotlin 2.1.20 + JUnit 5 + MockK, num projecto descartável com `buildSrc` (convention plugin), 2 módulos e relatório agregado.
> - ⚠️ A secção do `hls-providers` (Kotlin 2.3, Spring Boot 3.5.7, Nexus interno, detekt/Sonar) foi escrita a partir da leitura dos ficheiros de build, **não foi executada nesse repositório**.

---

## 1. Porquê PIT

- Standard de facto na JVM; plugins maduros para Maven (oficial) e Gradle (`info.solidsoft.pitest`, de terceiros) e suporte a JUnit 5.
- Alternativas: *Major* e *MuJava* são sobretudo académicos; *Descartes* é um *engine* que corre **dentro** do PIT (mutantes "extremos" para detectar métodos pseudo-testados).
- Funciona sobre bytecode, por isso também serve para Kotlin (ver §7.3).

| Componente | Versão usada | Nota |
|---|---|---|
| `org.pitest:pitest-maven` | 1.30.2 | Java 25 suportado desde 1.25.8 |
| `org.pitest:pitest-junit5-plugin` | 1.2.3 | **obrigatório** com JUnit 5 (sem ele, 0 testes) |
| `info.solidsoft.pitest` (Gradle) | 1.19.0 | só no Gradle Plugin Portal (Maven Central tem 1.15.0); Gradle ≥ 8.4 |
| `info.solidsoft.pitest.aggregator` | 1.19.0 | relatório agregado multi-módulo |

## 2. Boas práticas

1. **Mutar só código de produção** (`targetClasses`); nunca testes.
2. **Excluir o que não dá sinal**: classe `Application`, `@Configuration`/properties, DTOs/records triviais, código gerado.
3. **Excluir testes lentos** (`@SpringBootTest`, Testcontainers, e2e). O PIT executa a suite muitas vezes: precisa de testes rápidos e determinísticos.
4. **Não começar em 100%.** Medir a baseline, fixar o threshold uns pontos abaixo e subir gradualmente.
5. **Fora do build por omissão** (é lento): profile Maven / task própria no Gradle; CI nightly e/ou só nos módulos alterados em PR.
6. `avoidCallsTo` para logging (mutantes quase sempre equivalentes).
7. Ler os **mutantes sobreviventes como asserções fracas**, não como "falhas do PIT". Mutantes equivalentes existem: não perseguir 100%.
8. Em Reactor, testar com `StepVerifier` e asserções concretas (valor, erro, nº de subscrições): `assertNotNull`/`verifyComplete()` sozinhos deixam sobreviver quase tudo.

## 3. Projecto simples — Maven ✅

Aplicado em [`pom.xml`](../pom.xml), profile `mutation`:

```xml
<properties>
    <pitest.version>1.30.2</pitest.version>
    <pitest-junit5.version>1.2.3</pitest-junit5.version>
    <pitest.mutation-threshold>75</pitest.mutation-threshold>
</properties>

<profiles>
  <profile>
    <id>mutation</id>
    <build>
      <plugins>
        <plugin>
          <groupId>org.pitest</groupId>
          <artifactId>pitest-maven</artifactId>
          <version>${pitest.version}</version>
          <dependencies>
            <dependency>
              <groupId>org.pitest</groupId>
              <artifactId>pitest-junit5-plugin</artifactId>
              <version>${pitest-junit5.version}</version>
            </dependency>
          </dependencies>
          <configuration>
            <targetClasses>
              <param>io.github.jlmc.maydayhlsprovider.domain.*</param>
              <param>io.github.jlmc.maydayhlsprovider.application.*</param>
              <param>io.github.jlmc.maydayhlsprovider.adapters.*</param>
            </targetClasses>
            <excludedClasses>
              <param>io.github.jlmc.maydayhlsprovider.MaydayHlsProviderApplication</param>
              <param>io.github.jlmc.maydayhlsprovider.configuration.*</param>
            </excludedClasses>
            <excludedTestClasses>
              <param>*ApplicationTests</param>
              <param>*EndToEndTest</param>
            </excludedTestClasses>
            <avoidCallsTo><avoidCallsTo>org.slf4j</avoidCallsTo></avoidCallsTo>
            <mutators><mutator>STRONGER</mutator></mutators>
            <threads>4</threads>
            <outputFormats><outputFormat>HTML</outputFormat><outputFormat>XML</outputFormat></outputFormats>
            <timestampedReports>false</timestampedReports>
            <mutationThreshold>${pitest.mutation-threshold}</mutationThreshold>
          </configuration>
          <executions>
            <execution>
              <id>mutation-coverage</id>
              <phase>verify</phase>
              <goals><goal>mutationCoverage</goal></goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </build>
  </profile>
</profiles>
```

Correr:

```bash
./mvnw -Pmutation verify                                  # relatório em target/pit-reports/index.html
./mvnw -Pmutation -Dpitest.mutation-threshold=70 verify   # sobrepor o threshold (ex.: CI)
```

Nota: o threshold é uma *property* porque um valor fixo no `<configuration>` **não** é sobreposto por `-DmutationThreshold=…`.

**Baseline inicial (nov 2026):** 99 mutantes, 63 mortos (**64%**), line coverage das classes mutadas 89%, test strength 69%, ~30 s. Com threshold 70 o build falhava (`Mutation score of 64 is below threshold of 70`). ✅

**Depois de reforçar `RetryAfterHeaderParser` e `jitteredBackoff` com testes dedicados:** 79/99 mortos (**80%**), line coverage 94%, test strength 83%. Threshold subido para 75. O sobrevivente que resta no parser é equivalente (sem o `isBlank`, `"   "` falha no parse e devolve `Optional.empty()` na mesma).

## 4. Projecto simples — Gradle Kotlin DSL ✅

`build.gradle.kts` equivalente (validado em Gradle 8.10.2 / JDK 21; para este projecto Maven seria só ilustrativo):

```kotlin
plugins {
    java // ou kotlin("jvm")
    id("info.solidsoft.pitest") version "1.19.0"
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test { useJUnitPlatform() }

pitest {
    pitestVersion.set("1.30.2")        // fixar: o plugin tem a sua versão por omissão
    junit5PluginVersion.set("1.2.3")   // obrigatório com JUnit 5
    targetClasses.set(setOf("io.github.jlmc.maydayhlsprovider.domain.*", "io.github.jlmc.maydayhlsprovider.application.*", "io.github.jlmc.maydayhlsprovider.adapters.*"))
    excludedClasses.set(setOf("*.MaydayHlsProviderApplication", "*.configuration.*"))
    excludedTestClasses.set(setOf("*ApplicationTests", "*EndToEndTest"))
    avoidCallsTo.set(setOf("org.slf4j"))
    mutators.set(setOf("STRONGER"))
    threads.set(4)
    outputFormats.set(setOf("HTML", "XML"))
    timestampedReports.set(false)
    mutationThreshold.set(60)
}
```

```bash
./gradlew pitest        # relatório em build/reports/pitest/index.html
```

Diferenças face ao Maven: as propriedades são `Property<T>` (`.set(...)`); o plugin só funciona com a task `pitest`, que **não** está ligada a `check` (bom: mantém o build rápido). Aviso: o plugin Gradle é de terceiros e a compatibilidade com JDK muito recentes (ex.: 25) pode ficar atrás do PIT core — fixar `pitestVersion` e testar.

## 5. Spring / WebFlux

- Testes de slice/unitários (WebClient com WireMock/`MockWebServer`, `WebTestClient` sem contexto completo) são bons candidatos; `@SpringBootTest` + Testcontainers não (lentos; o PIT corre-os por mutante).
- Construtores com `@Autowired` implícito nos testes: `spring.test.constructor.autowire.mode=all` por `jvmArgs`/system property:
  - Maven: `<jvmArgs><value>-Dspring.test.constructor.autowire.mode=all</value></jvmArgs>`
  - Gradle: `jvmArgs.set(listOf("-Dspring.test.constructor.autowire.mode=all"))`
- Se os testes precisam de `--add-opens` (JDK 17+/21), repetir os mesmos no PIT (`jvmArgs`): o PIT lança uma JVM própria e **não** herda `tasks.test { jvmArgs(...) }`.
- Reactor: mutantes em operadores de retry/timeout só morrem com `StepVerifier` + relógio virtual ou asserções sobre nº de tentativas/atrasos (ver `WebClientProviderEncounterSearchAdapterTest`).

## 6. Multi-módulo — Maven

1. **Pai**: declarar `pitest-maven` + `pitest-junit5-plugin` em `<pluginManagement>` com as exclusões comuns, e um profile `mutation` que o activa só nos módulos com lógica.
2. **Cada módulo com lógica**: `targetClasses` próprios (por package) e, para agregar, as opções extra:

```xml
<exportLineCoverage>true</exportLineCoverage>
<outputFormats><value>XML</value></outputFormats>   <!-- XML é necessário; pode juntar HTML -->
```

3. **Módulo de relatório** (`*-mutation-report`) com dependências para os módulos a agregar e o goal `report-aggregate` (fase `verify`). É o mecanismo oficial ([Test Aggregation Across Modules](https://pitest.org/aggregating_tests_across_modules/)); o reactor garante a ordem porque o módulo de relatório depende dos restantes.
4. **Testes noutro módulo**: por omissão o PIT assume que os testes vivem no mesmo módulo do código. Há suporte limitado desde 1.17.1, com configuração explícita (ver quickstart oficial); caso contrário, um mutante que sobrevive num módulo pode ser morto por testes de outro e o relatório mente por excesso de pessimismo.
5. Nunca correr o PIT no módulo `*-app` (bootstrap/wiring).

## 7. Multi-módulo — Gradle Kotlin: `hls-providers` (monorepo)

### 7.1 Ponto de partida (lido em `/Users/jlmc/IdeaProjects/tdk/hls-providers`) ⚠️
- Gradle 8.10.2, ~29 módulos (`libs/*`, `common/*`, `middleware/*` (deprecated), `integrations/*-service` + `*-app`, `flink-jobs`, `smoke-tests`), `settings.gradle.kts` com `projectDir` remapeado.
- Kotlin 2.3 / Spring Boot 3.5.7 / JDK 21 (toolchain) / JUnit 5.12.2 / MockK + springmockk (sem Mockito) / WireMock.
- Configuração partilhada em **convention plugins em `buildSrc`**: `hls-providers.kotlin-conventions` (base, `tasks.test` com `--add-opens`, `dependsOn(detekt)` com `autoCorrect = true`), `…-common-conventions`, `…-library-conventions`, `…-application-conventions` (bootJar, `e2e`), `…integration-test-conventions` (`integrationTest`, Docker).
- JaCoCo agregado (≥ 80%) com lista `coverageExclusions` no `build.gradle.kts` raiz; Sonar; `org.gradle.caching=true`, daemon desligado.

### 7.2 Desenho recomendado
**Um convention plugin novo** (`hls-providers.mutation-conventions`), aplicado só aos módulos de lógica; agregação no raiz. Nada disto altera o `./gradlew build`: o PIT só corre pela task `pitest`.

`buildSrc/build.gradle.kts` — adicionar à lista `dependencies` existente (o `gradlePluginPortal()` já está nos `repositories`):

```kotlin
implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.19.0")
```

`buildSrc/src/main/kotlin/hls-providers.mutation-conventions.gradle.kts` (estrutura ✅ validada no projecto descartável; valores `hls-providers` ⚠️):

```kotlin
import info.solidsoft.gradle.pitest.PitestPluginExtension

plugins {
    id("hls-providers.kotlin-common-conventions")
    id("info.solidsoft.pitest")
}

configure<PitestPluginExtension> {
    pitestVersion.set("1.30.2")
    junit5PluginVersion.set("1.2.3")

    targetClasses.set(setOf("com.talkdesk.industries.hlsproviders.*"))

    // espelhar o `coverageExclusions` do build.gradle.kts raiz (config, dto, model, exception, filter,
    // property, Application, SOAP gerado, converters, *$logger$*) — ideal: mover essa lista para
    // buildSrc e partilhá-la entre JaCoCo e PIT.
    excludedClasses.set(setOf(
        "*.config.*", "*.dto.*", "*.model.*", "*.exception.*", "*.filter.*", "*.property.*",
        "*Application*", "*\$logger\$*",
        "com.epicsystems.interconnect_2004_05.*", "webservices2018.pharmacy.clinical.epic.*",
        "org.datacontract.schemas._2004._07.*", "com.microsoft.schemas._2003._10.serialization.*",
    ))

    avoidCallsTo.set(setOf("kotlin.jvm.internal", "mu", "io.github.oshai.kotlinlogging", "org.slf4j"))
    mutators.set(setOf("DEFAULTS"))          // começar por DEFAULTS; STRONGER gera mais ruído em Kotlin
    threads.set(4)
    outputFormats.set(setOf("HTML", "XML"))
    exportLineCoverage.set(true)             // obrigatório para pitestReportAggregate
    timestampedReports.set(false)
    mutationThreshold.set(0)                 // fixar por módulo depois de medir a baseline

    // O PIT lança JVM própria: repetir o que está em `tasks.test`
    jvmArgs.set(listOf(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.text=ALL-UNNAMED",
    ))

    // só testes unitários (`test`); NÃO incluir `integrationTest` nem `e2e` (Docker)
    testSourceSets.set(listOf(sourceSets["test"]))
}
```

Num módulo de lógica, p. ex. `modules/integrations/appointment-service/build.gradle.kts`:

```kotlin
plugins {
    id("hls-providers.kotlin-application-conventions")
    id("hls-providers.mutation-conventions")   // novo
    id("hls-providers.disable-runnable-tasks")
}
```

> Os módulos `*-app` mantêm-se de fora; módulos de lógica que hoje usam `kotlin-library-conventions` aplicam o mesmo plugin.

`build.gradle.kts` do raiz — agregação:

```kotlin
plugins {
    // …plugins existentes…
    // SEM versão: o plugin já está no classpath via buildSrc (com versão dá erro "already on the classpath")
    id("info.solidsoft.pitest.aggregator")
}

repositories {
    maven("https://hub.talkdeskapp.com:8443/repository/talkdesk-maven/")  // já existe; o aggregator resolve org.pitest:pitest-aggregator aqui
}
```

```bash
./gradlew :integrations:hls-providers-appointment-service:pitest      # um módulo (piloto)
./gradlew pitest pitestReportAggregate                                # tudo + relatório único em build/reports/pitest
```

Armadilhas encontradas na validação ✅:
- `pitestReportAggregate` falha com *"no lineCoverageFiles have been set"* se faltar `exportLineCoverage = true` nos módulos.
- Falha com *"no repositories are defined"* se o projecto raiz não declarar `repositories` (o `hls-providers` já declara).
- `id("info.solidsoft.pitest.aggregator") version "…"` no raiz entra em conflito com o plugin já presente no `buildSrc`; omitir a versão.

### 7.3 Kotlin, MockK e o ruído do PIT
- O PIT mutar bytecode Kotlin: aparecem mutantes em `when`, `isNullOrBlank`, `?:`, getters de `data class`. Os de `Intrinsics.check*` (null-checks) são o ruído clássico; `avoidCallsTo("kotlin.jvm.internal")` é a mitigação open-source. No projecto descartável **não surgiram mutantes de `Intrinsics`** com o PIT 1.30.2 (com ou sem a opção), por isso a opção é uma precaução: confirmar no primeiro módulo piloto olhando para `mutations.xml`.
- Para `data class`/`equals`/`hashCode`/`toString`/`copy` gerados, excluir os DTOs (`*.dto.*`, `*.model.*`) em vez de os testar.
- Coroutines e mapeamentos complexos podem produzir erros/ruído que só o plugin comercial Arcmutate Kotlin resolve; o `hls-providers` é Reactor (Mono/Flux), pelo que não é à partida um problema.
- MockK funciona normalmente (os testes corridos pelo PIT usaram MockK ✅). `springmockk` + `@SpringBootTest`: evitar no PIT (lento).

### 7.4 Cuidados específicos do repo ⚠️
- `tasks.test` depende de `detekt` com `autoCorrect = true` (altera fontes). A task `pitest` do plugin 1.19 não depende da task `test`, por isso não o dispara; ainda assim, correr `./gradlew test` antes para ter classes/testes compilados e detekt satisfeito, e **nunca** deixar o PIT correr com árvore de trabalho suja por auto-correcção.
- Memória: o daemon está desligado e `org.gradle.jvmargs=-Xmx4096m`; o PIT usa JVM(s) próprias (`threads` × heap). Para módulos grandes, `threads.set(2)` e `jvmArgs` com `-Xmx` explícito.
- `org.gradle.caching=true`: a task `pitest` é cacheável apenas se as entradas forem estáveis; `timestampedReports=false` ajuda.
- Dependências internas (Nexus `hub.talkdeskapp.com`): o plugin `info.solidsoft.pitest` vem do Gradle Plugin Portal (já em `buildSrc` repositories); `org.pitest:*` vem do Maven Central → confirmar que o Nexus faz *proxy* a Maven Central ou adicionar `mavenCentral()` aos `repositories` onde a configuração `pitest` é resolvida.
- Código gerado (`modules/integrations/medical-information-service/build/generated/java`, SOAP Epic) fora de `targetClasses`.

### 7.5 Estratégia de adopção
1. **Pilotos**: `libs/hls-providers-model` e `integrations/hls-providers-appointment-service`. Medir baseline, fixar `mutationThreshold` = baseline − ~5.
2. Alargar módulo a módulo (`common-apps`, `common-modules`, restantes `*-service`).
3. **CI**: job nightly com `./gradlew pitest pitestReportAggregate` e arquivo do `build/reports/pitest`; em PR, só os módulos alterados (`./gradlew :<módulo>:pitest`) ou histórico incremental. Nunca no `./gradlew build` normal.
4. Subir o threshold gradualmente; tratar sobreviventes nos ficheiros mais críticos (retry/limites/conversões).

## 8. CI (excerto)

```yaml
# GitHub Actions — Maven
- run: ./mvnw -B -Pmutation verify
- uses: actions/upload-artifact@v4
  with: { name: pit-report, path: target/pit-reports }

# GitHub Actions — Gradle
- run: ./gradlew pitest pitestReportAggregate
- uses: actions/upload-artifact@v4
  with: { name: pit-report, path: build/reports/pitest }
```

## 9. Troubleshooting

| Sintoma | Causa provável |
|---|---|
| 0 testes / 0% / "no tests" | falta `pitest-junit5-plugin` / `junit5PluginVersion` |
| Mutantes `NO_COVERAGE` em tudo | `excludedTestClasses` demasiado largo, ou testes noutro módulo |
| `TIMED_OUT` frequentes | testes com `sleep`/relógio real; usar relógio virtual; ajustar `timeoutFactor`/`timeoutConstant` |
| `MEMORY_ERROR`/OOM | baixar `threads`; `-Xmx` em `jvmArgs` |
| Erros de reflexão/`InaccessibleObjectException` | faltam `--add-opens` no `jvmArgs` do PIT |
| `Mutation score of X is below threshold of Y` | build a funcionar como previsto: reforçar testes ou rever threshold |
| Agregado Gradle vazio/erro | `exportLineCoverage` + XML nos módulos; `repositories` no raiz |

## 10. Caminho a seguir (roadmap)

Estado actual deste projecto: score **80%** (79/99), threshold **75**, 1 sobrevivente equivalente no parser.

> Exemplo do raciocínio usado para chegar aos 80%: [mutation-testing-worked-example.md](mutation-testing-worked-example.md).

### 10.1 Neste projecto (`mayday-hls-provider`)
| # | Passo | Critério de conclusão |
|---|---|---|
| 1 | Matar os sobreviventes restantes com valor: `FixedSizeTimeWindowSplitter` (limites das janelas, 3), `SearchEncountersService` (2), `ProviderRateLimitedException` (3), `TimeWindow`/`AccountId` (1 cada) | score ≥ 85% |
| 2 | Cobrir os `NO_COVERAGE` triviais (`ProviderClientErrorException`, `ProviderServerErrorException`, ramo em `GlobalExceptionHandler`) | 0 `NO_COVERAGE` em lógica |
| 3 | Subir o threshold por degraus (75 → 80 → 85), sempre ~5 pontos abaixo do score medido; nunca 100% | `./mvnw -Pmutation verify` verde |
| 4 | Ligar a um job CI nightly (e manual em PR) que publique `target/pit-reports` como artefacto | relatório acessível por run |
| 5 | Regra de equipa: PR que mexe em `domain`/`application`/`adapters` não pode baixar o score | threshold no CI |

Regras de ouro: um sobrevivente é um teste por escrever **ou** um mutante equivalente (documentar e ignorar); nunca baixar o threshold para fazer o build passar sem registar porquê.

### 10.2 Replicação no `hls-providers` (Gradle Kotlin, multi-módulo) ⚠️ não executado
1. **Spike (½ dia)**: adicionar `hls-providers.mutation-conventions` (§7.2) e correr `:libs:hls-providers-model:pitest`; confirmar resolução do plugin/`org.pitest:*` no Nexus, Java 21, MockK e o ruído Kotlin (§7.3).
2. **Piloto 1–2 módulos** (`libs/model`, `integrations/appointment-service`): baseline, `mutationThreshold` = baseline − 5, partilhar `coverageExclusions` entre JaCoCo e PIT.
3. **Agregação** no raiz (`pitestReportAggregate`) e job nightly no CI (Jenkins/GitHub Actions); em PR só o módulo alterado.
4. **Alargar** módulo a módulo (`common-*`, restantes `*-service`); `*-app`, `smoke-tests`, `flink-jobs` e `middleware` (deprecated) ficam de fora.
5. **Gate** gradual: primeiro informativo (sem falhar o build), depois com threshold por módulo.
6. Decidir mais tarde, com dados do piloto, se o ruído Kotlin justifica avaliar o plugin comercial Arcmutate ou o `pitest-kotlin`.

### 10.3 Riscos a vigiar
- Tempo de execução em módulos grandes (usar `threads`, histórico incremental, só módulos alterados).
- Testes `@SpringBootTest`/Testcontainers a entrar sem querer no PIT (rever `excludedTestClasses`).
- Score inflacionado por exclusões a mais: rever a lista de exclusões a cada trimestre.
- Plugin Gradle de terceiros atrás do PIT core em versões de JDK novas.

## 11. Leituras recomendadas

Artigos encontrados na pesquisa. **Cada um tem uma ficha de resumo em [readings/](readings/README.md)**, com o grau de leitura real (alguns papers só foram lidos em parte, e a página do BrowserStack já não existe).

### Introdução ao conceito
- [Introduction to Mutation Testing (Mull)](https://mull.readthedocs.io/en/0.19.0/MutationTestingIntro.html): curto e técnico, bom ponto de partida · [ficha](readings/01-mull-introduction.md)
- [An Introduction to Mutation Testing (Cornell)](https://www.cs.cornell.edu/~dgeisler/mutation/testing/2021/11/01/mutation-testing1.html): teoria e limites da técnica · [ficha](readings/02-cornell-introduction.md)
- [What is mutation testing? (TechTarget)](https://www.techtarget.com/it-infrastructure/definition/mutation-testing): definição geral · [ficha](readings/03-techtarget-definition.md)
- ~~BrowserStack~~: removido, a página devolve HTTP 410 · [nota](readings/04-browserstack-guide.md)

### Mutation testing em escala (papers e podcast)
- [State of Mutation Testing at Google](https://research.google/pubs/state-of-mutation-testing-at-google/) (ICSE-SEIP 2018): mutantes só para o código alterado, durante o code review. É a base da estratégia "incremental em PR" do roadmap (§10). · [ficha (só resumo lido)](readings/05-google-state-of-mutation-testing-2018.md)
- [Practical Mutation Testing at Scale (IEEE TSE 2021)](https://homes.cs.washington.edu/~rjust/publ/practical_mutation_testing_tse_2021.pdf): follow-up do Google, com cerca de 17 milhões de mutantes. · [ficha (leitura parcial)](readings/06-google-practical-mutation-testing-at-scale-2021.md)
- [What It Would Take to Use Mutation Testing in Industry: A Study at Facebook](https://arxiv.org/pdf/2010.13464): a mesma abordagem do lado do Facebook. · [ficha (leitura parcial)](readings/07-facebook-industry-study.md)
- [SE Radio 632: Goran Petrović sobre mutation testing no Google](https://se-radio.net/2024/09/se-radio-632-goran-petrovic-on-mutation-testing-at-google/): podcast. · [ficha (leitura parcial)](readings/08-se-radio-632-petrovic.md)

### Java, PIT, Spring e Kotlin
- [Test your tests: mutation testing in Java with PIT (JAVAPRO, 2026)](https://javapro.io/2026/01/21/test-your-tests-mutation-testing-in-java-with-pit/) · [ficha](readings/09-javapro-pit-2026.md)
- [Mutation testing em Spring Boot 3 (Paradigma)](https://en.paradigmadigital.com/dev/mutation-testing-spring-boot-3-projects-beat-slowness-supercharge-tests/) · [ficha](readings/10-paradigma-spring-boot-3.md)
- [Experimental: Kotlin and mutation testing (Frankel, 2016, desactualizado)](https://blog.frankel.ch/experimental-kotlin-mutation-testing/) · [ficha](readings/11-frankel-kotlin-2016.md)

Pesquisa descartada: as páginas de educative.io e appsierra.com sobre o tema continham erros (misturam mutações genéticas com mutation testing de software).

## 12. Fontes
- [PIT — Maven quickstart](https://pitest.org/quickstart/maven/) · [Test Aggregation Across Modules](https://pitest.org/aggregating_tests_across_modules/) · [Downloads](https://pitest.org/downloads)
- [gradle-pitest-plugin (README, CHANGELOG)](https://github.com/szpak/gradle-pitest-plugin) · [Plugin Portal](https://plugins.gradle.org/plugin/info.solidsoft.pitest)
- [PIT + Kotlin: issue #579](https://github.com/hcoles/pitest/issues/579) · [Experimental: Kotlin and mutation testing](https://blog.frankel.ch/experimental-kotlin-mutation-testing/)
- [Test your tests: mutation testing in Java with PIT (JAVAPRO, 2026)](https://javapro.io/2026/01/21/test-your-tests-mutation-testing-in-java-with-pit/)
- [Mutation testing em Spring Boot 3 (Paradigma)](https://en.paradigmadigital.com/dev/mutation-testing-spring-boot-3-projects-beat-slowness-supercharge-tests/)
