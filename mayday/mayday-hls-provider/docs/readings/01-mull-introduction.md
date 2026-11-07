# Introduction to Mutation Testing (documentação do Mull)

- **Fonte:** https://mull.readthedocs.io/en/0.19.0/MutationTestingIntro.html
- **Tipo:** documentação de uma ferramenta (Mull, para C/C++ sobre LLVM, não para Java)
- **Leitura:** página completa lida.

## Tese
Mutation testing mede a qualidade de uma suite de testes: cria versões ligeiramente alteradas do programa (mutantes) e vê se os testes as detectam.

## Pontos de maior interesse
- É uma técnica baseada em falhas (*fault-based*): mede se os testes **verificam** comportamento, não apenas se executam código.
- Cada mutante nasce de um **operador de mutação**: uma regra que altera ou remove uma instrução ou expressão.
- **Morto** = pelo menos um teste falha. **Sobrevivente** = todos passam, o que indica uma lacuna.
- Os operadores são determinísticos: o mesmo código com os mesmos operadores gera sempre os mesmos mutantes.
- **Mutation score** = mortos / total. O único exemplo numérico da página: 7 mortos em 10 dá 0,7.
- Serve para revelar lacunas de cobertura *semântica*.

## Limites
A página não trata custos de tempo, mutantes equivalentes nem falsos positivos, e não lista operadores concretos (remete para outras páginas, não lidas).

## Para a nossa equipa
- Boa definição curta para onboarding. Atenção: **não fala de Java nem de PIT**; a equivalência com o PIT é conhecimento nosso, não da página.
- Ideias a validar (sugestões nossas): usar o score como indicador e não como meta; analisar sobreviventes à procura de asserts em falta; limitar a execução ao código alterado para controlar o tempo de CI.
