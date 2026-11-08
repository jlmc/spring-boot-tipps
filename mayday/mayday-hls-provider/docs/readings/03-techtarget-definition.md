# What is mutation testing? (TechTarget, Alexander Gillis, 14-04-2023)

- **Fonte:** https://www.techtarget.com/it-infrastructure/definition/mutation-testing
- **Tipo:** definição de glossário (introdutório)
- **Leitura:** artigo completo lido.

## Em resumo
**Tópico:** definição e vocabulário.
- Três famílias de mutantes: instrução, valor e decisão.
- Score = mortos / total × 100; os equivalentes ficam fora.
- Cita o PIT como ferramenta Java.
- Exagera ao tratar 100% como "testes adequados".

## Tese
Mutation testing avalia a qualidade dos testes introduzindo pequenas alterações no código e verificando se a suite as detecta.

## Pontos de maior interesse
- Teste de **caixa branca**, usado sobretudo em testes unitários.
- Mutante **morto** = os testes passam a dar resultado diferente do original. Sobreviventes indicam código mal testado ou testes fracos.
- Mutantes **equivalentes** têm a mesma semântica e não entram no score.
- Três famílias: de **instrução** (remover/substituir), de **valor** (alterar constantes) e de **decisão** (trocar operadores, p. ex. `<` por `>`).
- **Score = mortos / total × 100.**
- Ferramentas citadas: Insure++, Jester (JUnit), **PIT** (Java), MuClipse (Eclipse).

## Limites
- Curto, sem dados empíricos nem números de desempenho; não cobre custos em CI nem formas de reduzir mutantes.
- Sem automação não é prático: o número de mutantes torna o processo caro e lento.
- O texto trata 100% de score como testes "adequados": é exagero. 100% só prova que os mutantes **gerados** morreram, não que o comportamento está completo. A fórmula tem também uma gralha no denominador.

## Para a nossa equipa
Boa referência de vocabulário (instrução/valor/decisão). Sugestões nossas: score como indicador e não como meta absoluta; concentrar-se em validações, condições e cálculos.

## Aprofundamento

**Os seis passos do artigo.**
1. Escrever um teste unitário.
2. Escrever o código que o faz passar.
3. Correr o teste mutacional sobre o código, garantindo que código e teste funcionam.
4. Criar mutações (**cada mutante com um só erro**) e correr o código mutado contra o teste.
5. Comparar resultados: se diferirem, o mutante foi detectado; se forem iguais, o teste falhou e o mutante é guardado.
6. Score = mortos / total × 100.

**Exemplos por família (do artigo).**
- *Instrução:* `A = 10 by B = 5` passa a `A = 5 by B = 15`.
- *Valor:* `A = 15` passa a `A = 10` ou `A = 25`.
- *Decisão:* `(A < B)` passa a `(A > B)`.

**Equivalente:** mesmo significado do original, apesar de sintaxe diferente; fora do cálculo do score.

**Ferramentas citadas:** Insure++, Jester (para JUnit), PIT (Java/JVM), MuClipse (Eclipse); o artigo só as lista, sem as descrever.

**Inferências nossas (não estão no artigo):** o Insure++ parece ser uma ferramenta comercial (Parasoft), ao contrário do que a classificação "open source" sugere; Jester muta Java e corre JUnit; PIT muta bytecode e integra com Maven/Gradle; detectar equivalentes é indecidível, por isso o score tem sempre alguma incerteza.

**Por ler:** os links internos (regression testing, white box testing) e o artigo citado sobre testes unitários.

**No nosso projecto.** O passo 4 ("um só erro por mutante") é o que o PIT faz: cada um dos 99 mutantes do nosso report altera uma coisa só, o que permite saber exactamente que linha ficou sem asserção.
