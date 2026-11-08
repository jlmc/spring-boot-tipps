# Experimental: Kotlin and mutation testing (Nicolas Fränkel, 3-04-2016)

- **Fonte:** https://blog.frankel.ch/experimental-kotlin-mutation-testing/
- **Tipo:** blog de experiência pessoal
- **Leitura:** artigo completo lido (HTTP 200).
- **Aviso: tem mais de 10 anos** (Kotlin, KTor e PIT de 2016). Os problemas descritos podem estar resolvidos; **verificar antes de decidir**.

## Em resumo
**Tópico:** PIT com Kotlin.
- Funciona, com ruído do bytecode gerado (`Intrinsics`) e falhas com `when`.
- Solução do autor: excluir as classes geradas.
- É de 2016: no nosso demo com PIT 1.30.2 o ruído não apareceu.

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

## Aprofundamento

**Projecto de demonstração.** *Artigo:* um port para Kotlin do seu projecto de demonstração (Maven), com duas classes simples e um teste para cada. Numa falta uma asserção; na outra a condição de fronteira do operador `<` não está testada. O PIT aponta as linhas dos dois casos (as capturas têm nomes como `duplicatemath.png` e `lowpredicate.png`; as imagens não foram vistas). *Inferência nossa:* o mutante típico é `<` por `<=`, que sobrevive se nenhum teste usar o valor de fronteira.

**Comandos exactos (do artigo).**
- Demo: `mvn org.pitest:pitest-maven:mutationCoverage`
- KTor (restringido a um módulo, `ktor-server-sessions`): o mesmo comando.
- Com exclusão: o mesmo comando com `-DexcludedClasses=org.jetbrains.ktor.locations.Locations*`.

**A classe `WhenMappings`, passo a passo.**
- *Artigo:* ao correr o PIT sobre `ktor-locations`, falha porque a classe `Locations$WhenMappings` não tem informação de debug (fonte e número de linha), que o PIT exige; o autor atribui a causa ao uso de `when`.
- *Inferência nossa:* o compilador Kotlin gera uma classe auxiliar aninhada, normalmente quando o `when` ramifica sobre um `enum`; é sintética e não tem linhas de código-fonte; o PIT percorre todas as classes do módulo e para ao encontrar uma sem essa informação; a solução é excluí-la com o padrão `Locations*`.

**Mutações de `Intrinsics`.**
- *Artigo:* aparece uma mutação de "remoção de chamada" a `kotlin/jvm/internal/Intrinsics::checkParameterIsNotNull`, com estado `NO_COVERAGE`; o autor diz que os erros são "semelhantes aos esperados em Java".
- *Inferência nossa:* o Kotlin injecta estas verificações de nulos em funções públicas; não é código do programador e nenhum teste as exercita directamente. Remover a chamada não muda o comportamento de forma detectável: é ruído.

**Avisos de mutações duplicadas em blocos `finally`.** *Artigo:* o PIT avisa que encontrou mais do que uma mutação semelhante na mesma linha dentro de um `finally` e que não consegue corrigir o *inlining*; também aparecem avisos de `TIMED_OUT` ("Slave exited abnormally"), que o autor não considera bloqueantes. *Inferência nossa:* o Kotlin copia o bloco `finally` para cada caminho de saída, logo a mesma instrução existe várias vezes no bytecode e o PIT trata cada cópia como um mutante separado.

**No nosso projecto.** Na validação com PIT 1.30.2 num demo Kotlin pequeno **não** apareceu nenhum mutante de `Intrinsics`; não testámos `when` sobre `enum` nem `finally`, que são os dois casos que o artigo refere. Convém testá-los no módulo piloto do `hls-providers`.

**Por ler:** as capturas de ecrã, o código exacto das classes do demo e as versões de Kotlin/KTor/PIT usadas (o artigo não os dá).
