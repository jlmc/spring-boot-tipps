# An Introduction to Mutation Testing (Dietrich Geisler, Cornell, nov. 2021)

- **Fonte:** https://www.cs.cornell.edu/~dgeisler/mutation/testing/2021/11/01/mutation-testing1.html (série de 3 partes: 1, 7 e 8 de novembro de 2021)
- **Tipo:** blog académico (teoria e limites)
- **Leitura:** as três partes lidas; uma frase da parte 3, na secção sobre equivalência, estava cortada.

## Em resumo
**Tópico:** teoria e limites da técnica.
- Asserções apertadas importam: `x == 21` mata o mutante que `x > 0` deixa passar.
- Duas hipóteses sustentam a técnica: o programador competente e o efeito de acoplamento.
- Mutantes equivalentes nunca morrem, e provar equivalência é indecidível.
- O custo é o maior obstáculo: a suite corre uma vez por mutante.

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

## Aprofundamento

**O processo (parte 2, descrição do diagrama).**
1. Partir de um programa P e de uma suite T que já existe e passa.
2. Gerar os mutantes P' a partir de P (passo independente dos testes).
3. Correr T sobre cada mutante ainda "vivo".
4. Os mortos saem do conjunto; se todos morrerem, a análise acaba.
5. Os sobreviventes são avaliados: se não forem equivalentes, reforça-se T e o ciclo recomeça.

**Exemplo central.** `foo(x) = x + 1`, testado com `foo(20)`. Com a asserção `resultado > 0`, o mutante `x - 1` (dá 19) continua a satisfazê-la e sobrevive. Com `resultado == 21`, morre. Nota: o texto diz primeiro "x > 20" e depois "x == 21"; o valor certo é 21, a primeira formulação é uma inconsistência do próprio artigo.

**Equivalentes.** `x * 1` e `x + 0` não alteram o comportamento do código que mutam; nenhum teste os mata. O artigo não dá números.

**Ordem superior.** Mutantes com mais de uma alteração; um estudo citado diz que são mais difíceis de matar e melhoram a cobertura, mas custam mais a gerar.

**Explosão de testes (parte 3).** Escrever um teste por mutante gera muitos testes triviais; o autor recomenda testes que cubram vários mutantes relacionados.

**As duas hipóteses.**
- *Programador competente:* o código fica próximo do pretendido, logo mutantes simples imitam erros reais. O artigo admite que há poucos resultados verificados.
- *Efeito de acoplamento:* uma suite que apanha falhas simples tende a apanhar as complexas (probabilístico, não absoluto). Único número da série: testes feitos para matar mutantes simples mataram ~99% dos complexos.

**Reduzir custo (e o que se sacrifica).** *Weak mutation* (verificar componentes em vez do programa todo: perde-se fidelidade, inferência nossa); mutar **bytecode** (MuJava: os mutantes deixam de ter correspondência directa com o código-fonte, inferência nossa); **LLVM bitcode** (Mull: o artigo diz que a documentação admite perda de precisão); **paralelização** (custo de infra, inferência nossa). O artigo diz ser difícil medir o ganho real destas técnicas.

**No nosso projecto.** O `foo(x)` é o nosso `jitteredBackoff`: o teste antigo só confirmava "houve retry" (o `> 0` do exemplo) e nunca **quanto** se esperou. Passar a afirmar valores exactos (100, 200, 400, 800, 1000 ms) é o `== 21`.

**Notas de leitura.** A frase cortada na secção de equivalência da parte 3 **está cortada na própria página** (relida duas vezes com o mesmo resultado); uma ligação da parte 2 aponta para `127.0.0.1:4000`, sinal de publicação a partir de rascunho. As referências bibliográficas (surveys, Kintis, Malevris) não foram seguidas.
