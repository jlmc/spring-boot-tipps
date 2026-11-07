# State of Mutation Testing at Google (Petrović & Ivanković, ICSE-SEIP 2018)

- **Fontes:** https://research.google/pubs/state-of-mutation-testing-at-google/
- **Tipo:** paper industrial
- **Leitura: SÓ O RESUMO (abstract).** O PDF não foi lido (extracção devolveu binário). Nada abaixo vem do corpo do paper; para operadores, supressão de mutantes, ciclo de feedback e limitações, ver a ficha do follow-up de 2021, que cobre a mesma abordagem: [06](06-google-practical-mutation-testing-at-scale-2021.md).

## Tese (do resumo)
Mutation testing é um dos critérios de teste mais fortes mas é computacionalmente proibitivo à escala industrial. A solução do Google é uma abordagem **probabilística baseada em diffs**, mostrada no code review.

## Pontos do resumo
- Analisar **só as linhas alteradas**, e não o programa inteiro.
- Excluir linhas **sem cobertura** de instruções.
- Excluir linhas "**áridas**" (pouco interessantes), com uma heurística que depende da linguagem.
- Seleccionar só os mutantes mais interessantes, para que as pessoas consigam avaliar o resultado.
- O foco é o **code review**: resultados mostrados ao autor e ao revisor; estuda-se o efeito disso na atenção dos programadores.

## Números (do resumo)
Cerca de 6000 engenheiros do Google usam-no em todas as alterações que escrevem ou revêem; afecta mais de 14 000 autores; processa cerca de 30% dos diffs com cobertura calculada; faz parte do processo obrigatório de revisão.

## Limites
Não lidos (o resumo não os indica).

## Para a nossa equipa (sugestões nossas)
A ideia útil e verificável a partir do resumo: **mutar só o código alterado do PR** e excluir linhas sem cobertura. Confirmar na documentação do PIT o suporte (o histórico incremental e o `scmMutationCoverage` são os candidatos, não verificados aqui).
