# An Introduction to Mutation Testing (Dietrich Geisler, Cornell, nov. 2021)

- **Fonte:** https://www.cs.cornell.edu/~dgeisler/mutation/testing/2021/11/01/mutation-testing1.html (série de 3 partes: 1, 7 e 8 de novembro de 2021)
- **Tipo:** blog académico (teoria e limites)
- **Leitura:** as três partes lidas; uma frase da parte 3, na secção sobre equivalência, estava cortada.

## Tese
Mutation testing avalia a qualidade dos testes: altera o código de propósito e verifica se os testes dão por isso.

## Pontos de maior interesse
- **Asserções apertadas importam.** Exemplo do autor: com `foo(x) = x + 1` e o teste `foo(20) > 0`, o mutante `x - 1` (dá 19) sobrevive; com `foo(20) == 21` morre. É exactamente o que vimos no `jitteredBackoff`.
- Duas hipóteses sustentam a técnica: a **hipótese do programador competente** (o código está perto do correcto) e o **efeito de acoplamento** (testes que matam mutantes simples tendem a matar os complexos).
- Ciclo: gerar mutantes → correr testes → analisar sobreviventes → melhorar testes → repetir.
- **Mutantes de ordem superior** (várias alterações) são mais difíceis de matar mas caros de gerar.
- **Mutantes equivalentes** (`x * 1`, `x + 0`) nunca morrem; provar equivalência é indecidível em geral, só há heurísticas.
- O maior obstáculo é o **desempenho**: a suite corre uma vez por mutante. As aproximações (weak mutation, mutar bytecode, paralelizar) trocam precisão por velocidade.
- Complementa testes unitários e fuzzing; compensa quando já há testes, a cobertura tem de crescer e o código vai mudar.

## Números
Um estudo citado: testes feitos para matar mutantes simples mataram cerca de 99% dos mais complexos (apoia o efeito de acoplamento).

## Limites
A hipótese do programador competente tem poucos resultados verificados; o acoplamento é probabilístico; mutantes redundantes distorcem o score; exige uma base de testes razoável.

## Para a nossa equipa
- Não menciona o PIT (para Java cita o MuJava, ao nível do bytecode); a ligação ao PIT é nossa.
- Reforça duas regras do guia: asserções com **valores concretos** e esforço na **lógica de negócio**, não em DTOs/getters.
