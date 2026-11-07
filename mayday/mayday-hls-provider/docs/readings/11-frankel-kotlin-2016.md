# Experimental: Kotlin and mutation testing (Nicolas Fränkel, 3-04-2016)

- **Fonte:** https://blog.frankel.ch/experimental-kotlin-mutation-testing/
- **Tipo:** blog de experiência pessoal
- **Leitura:** artigo completo lido (HTTP 200).
- **Aviso: tem mais de 10 anos** (Kotlin, KTor e PIT de 2016). Os problemas descritos podem estar resolvidos; **verificar antes de decidir**.

## Tese
Não havendo ferramenta de mutation testing dedicada a Kotlin, o PIT (feito para Java) funciona razoavelmente, com algum ruído e falhas em certos casos; vale a pena usá-lo.

## Pontos de maior interesse
- Motivação: cobertura é fácil de medir mas pouco significativa.
- Num projecto de demonstração portado para Kotlin (Maven), o PIT encontrou a asserção em falta e o limite de um `<` não testado, com as linhas.
- Num projecto real (módulos do KTor), apareceram avisos de mutações duplicadas em blocos `finally` (por causa do *inlining*) e muitos timeouts.
- **Ruído do bytecode gerado:** mutações como a remoção das chamadas a `kotlin/jvm/internal/Intrinsics` (verificação de parâmetros não nulos), marcadas `NO_COVERAGE`. Não são falhas reais dos testes.
- **Falha com `when`:** gera uma classe interna (`WhenMappings`) sem informação de debug, que o PIT exige. Solução do autor: excluir essas classes.
- Não menciona plugins nem extensões; usa só o `pitest-maven`.

## Números
Nenhuns quantitativos; resultados qualitativos.

## Limites
Experiência de uma pessoa, sem métricas; versões antigas.

## Para a nossa equipa (sugestões nossas, não do artigo)
- Relevante para o `hls-providers` (monorepo Kotlin). Correr num módulo piloto primeiro e inspeccionar `mutations.xml`.
- **Nota da nossa validação:** com o PIT 1.30.2 num demo Kotlin **não apareceram** mutantes de `Intrinsics`, o que sugere que o problema de 2016 está atenuado; mantemos `avoidCallsTo("kotlin.jvm.internal")` como precaução.
- Excluir classes geradas e limitar o alvo à lógica de domínio reduz timeouts e falsos positivos.
