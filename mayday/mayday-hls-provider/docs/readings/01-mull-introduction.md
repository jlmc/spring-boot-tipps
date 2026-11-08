# Introduction to Mutation Testing (documentação do Mull)

- **Fonte:** https://mull.readthedocs.io/en/0.19.0/MutationTestingIntro.html
- **Tipo:** documentação de uma ferramenta (Mull, para C/C++ sobre LLVM, não para Java)
- **Leitura:** página completa lida.

## Em resumo
**Tópico:** o que é mutation testing.
- Cria versões alteradas do código (mutantes) e vê se os testes as detectam.
- Mutante morto = algum teste falha; sobrevivente = todos passam, sinal de lacuna.
- Score = mortos / total (exemplo: 7 em 10 dá 0,7).
- Não fala de Java nem de PIT (ferramenta de C/C++).

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

## Aprofundamento

**Mecanismo.** Do artigo: um operador (*mutator*) é uma regra fixa que altera ou remove uma instrução; aplicam-se os operadores ao programa, obtêm-se os mutantes, corre-se a suite contra cada um. Os operadores são determinísticos: mesmo código, mesmos operadores, mesmos mutantes. *Inferência nossa:* o passo-a-passo concreto (localizar pontos, aplicar, compilar, executar, classificar) é a implementação habitual, mas a página não o descreve nem diz como trata mutantes que não compilam.

**Exemplos do artigo.** Só um, numérico: 7 mortos em 10 dá score 0,7. Não há código antes/depois na introdução.

**Operadores reais** (página irmã *SupportedMutations*, também lida; cerca de 50 operadores de C/C++, prefixo `cxx_`):

| Operador | O que faz |
|---|---|
| `cxx_add_to_sub` | troca `+` por `-` |
| `cxx_eq_to_ne` | troca `==` por `!=` |
| `cxx_lt_to_le` | troca `<` por `<=` |
| `cxx_logical_and_to_or` | troca `&&` por `\|\|` |
| `cxx_remove_void_call` | remove chamadas a funções `void` |
| `cxx_replace_scalar_call` | substitui uma chamada por `42` |
| `cxx_remove_negation` | `!a` passa a `a` |

São os mesmos tipos de mutação que o PIT faz em Java (fronteiras, aritmética, remoção de chamadas `void`).

**Exemplo ilustrativo** *(nosso, não do artigo)*: `if (idade > 18)` passa a `if (idade >= 18)`. Só um teste com `idade = 18` falha, e só então o mutante morre.

**No nosso projecto.** É exactamente o caso de `RetryAfterHeaderParser`: `seconds >= 0` ↔ `seconds > 0`. Só o teste com o header `"0"` mata o mutante de fronteira.

**Por ler:** *HowMullWorks* e *IncrementalMutationTesting* (as mais relevantes para custo e mecanismo), Features, Tutorials e a referência de linha de comandos.
