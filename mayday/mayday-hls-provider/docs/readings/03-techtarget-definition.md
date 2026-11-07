# What is mutation testing? (TechTarget, Alexander Gillis, 14-04-2023)

- **Fonte:** https://www.techtarget.com/it-infrastructure/definition/mutation-testing
- **Tipo:** definição de glossário (introdutório)
- **Leitura:** artigo completo lido.

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
