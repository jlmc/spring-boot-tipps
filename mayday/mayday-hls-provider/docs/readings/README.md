# Leituras sobre mutation testing: índice e conclusões

Uma ficha por artigo, em português, com os pontos mais importantes e uma secção "Para a nossa equipa". As fichas são **resumos nossos**, sem citações longas; para detalhes e números exactos, consultar a fonte.

## Grau de leitura (importante)

| # | Ficha | Tipo | Leitura |
|---|---|---|---|
| 01 | [Mull: introdução](01-mull-introduction.md) | doc de ferramenta (C/C++) | completa |
| 02 | [Cornell: introdução (3 partes)](02-cornell-introduction.md) | blog académico | completa (1 frase cortada) |
| 03 | [TechTarget: definição](03-techtarget-definition.md) | glossário | completa |
| 04 | [BrowserStack](04-browserstack-guide.md) | guia | **não lido** (página removida, HTTP 410) |
| 05 | [Google 2018](05-google-state-of-mutation-testing-2018.md) | paper | **só o resumo** |
| 06 | [Google 2021](06-google-practical-mutation-testing-at-scale-2021.md) | paper | **parcial** (~30%) |
| 07 | [Facebook](07-facebook-industry-study.md) | paper | **parcial** (~57%) |
| 08 | [SE Radio 632](08-se-radio-632-petrovic.md) | podcast | **parcial** (~52% do texto) |
| 09 | [JAVAPRO: PIT (2026)](09-javapro-pit-2026.md) | artigo prático | completa |
| 10 | [Paradigma: Spring Boot 3](10-paradigma-spring-boot-3.md) | caso prático | completa |
| 11 | [Fränkel: Kotlin (2016)](11-frankel-kotlin-2016.md) | blog | completa (**antigo**) |

Nas fichas parciais, o que veio de resumos de modelo está marcado **[resumo]** ou **(resumo)**. Os PDFs dos papers não puderam ser lidos directamente.

## Conclusões transversais

1. **Cobertura mede execução, mutation testing mede verificação.** Todas as fontes concordam. (01, 02, 03, 09)
2. **Os sobreviventes são o produto útil**; o score é só indicador. Perseguir 100% é desperdício por causa dos equivalentes. (02, 03, 08, 09)
3. **O custo é o obstáculo principal**, e as soluções convergem: só testes unitários rápidos, só código alterado, excluir código gerado, execução fora do pipeline principal. (02, 06, 09, 10)
4. **Ruído mata a adopção.** O Google só teve sucesso quando filtrou mutantes inúteis (logging, timeouts, flags) e limitou o que mostra por revisão: a "utilidade" passou de ~15% para ~89%. (05, 06, 08)
5. **Mostrar poucos mutantes no momento certo (no PR) funciona melhor do que um relatório global.** (05, 06, 07, 08)
6. **Asserções concretas matam mais mutantes do que mais testes.** O caso Paradigma (validar o corpo e não só o status) e o nosso `jitteredBackoff` são o mesmo fenómeno. (02, 09, 10)
7. **Kotlin traz ruído de bytecode**, mas a fonte é de 2016; a nossa validação com PIT 1.30.2 não o reproduziu. (11)

## O que já aplicámos / o que falta neste projecto

| Ideia | Fonte | Estado |
|---|---|---|
| Excluir testes Spring/Testcontainers e código de wiring | 09, 10 | feito (`excludedTestClasses`, `excludedClasses`) |
| Excluir logging das mutações | 06, 08 | feito (`avoidCallsTo org.slf4j`) |
| Reforçar asserções em vez de baixar o threshold | 02, 10 | feito (`jitteredBackoff`, `RetryAfterHeaderParser`: 64% → 80%) |
| Threshold como piso de regressão, não como meta | 09, 06 | feito (75, abaixo do score medido) |
| Mutar só o código alterado do PR | 05, 06, 07, 08 | **por fazer** (ver o roadmap do guia, §10) |
| `withHistory` para execuções incrementais | 09, 10 | por avaliar |
| Descartes (menos mutantes) | 10 | por avaliar num módulo, com cautela (esteve parado vários anos) |
| Mutadores próprios a partir de bugs da equipa | 07 | ideia futura |

## Cuidados a ter ao citar estes artigos
- Os números do Google e do Facebook são **do contexto deles** (cultura de code review, monorepo próprio); não se transferem automaticamente.
- As metas "60–80%" e ">90% de test strength" do JAVAPRO não têm fonte; o "80%" do Paradigma não é justificado.
- O texto da TechTarget exagera ao tratar 100% de score como testes "adequados".
