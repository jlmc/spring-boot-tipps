# Practical Mutation Testing at Scale: A view from Google (Petrović, Ivanković, Fraser, Just; IEEE TSE 2021)

- **Fontes:** https://homes.cs.washington.edu/~rjust/publ/practical_mutation_testing_tse_2021.pdf · versão lida: https://ar5iv.labs.arxiv.org/html/2102.11378
- **Tipo:** paper industrial (follow-up do de 2018)
- **Leitura: PARCIAL.** O PDF não deu texto. Da versão ar5iv leram-se cerca de 39 000 de 131 000 caracteres (introdução e secções II–III). O resto chegou como resumo gerado por modelo; os pontos marcados **[resumo]** assentam só nisso. Resultados detalhados, apêndice e ameaças à validade não foram lidos directamente.

## Tese
Mutation testing é viável à escala industrial se se limitar às **linhas alteradas e cobertas**, se **filtrar mutantes que os programadores acham improdutivos** e se mostrar **poucos sobreviventes** no code review.

## Pontos de maior interesse
- **Baseado em diffs:** só se mutam linhas alteradas e cobertas por testes.
- **Nós "áridos":** nós da AST sem valor (logging, reservas de memória, timeouts, flags) são marcados antes e nunca geram mutantes. Um nó composto só é árido se todos os filhos o forem.
- Mais de **100 regras manuais**, alimentadas por feedback dos programadores. Algumas são *unsound* (podem suprimir mutantes úteis) e os autores aceitam esse compromisso.
- **Operadores:** AOR, LCR, ROR, UOI, SBR. O ABS foi excluído por gerar sobretudo mutantes improdutivos.
- **Selecção:** um mutante por linha, com operadores em ordem aleatória; **no máximo 7 mutantes por ficheiro**, escolhidos entre os sobreviventes.
- Mostrados como *findings* no code review, com botões de um clique ("Please fix" / "Not useful").
- **Produtividade:** cerca de 85% dos mutantes iniciais eram improdutivos; com as heurísticas a produtividade foi de ~15% para ~89%.
- Um mutante apresentado **não é um bug**: a medida é o juízo do programador sobre utilidade.

## Números
- Escala: ~2 mil milhões de linhas; mais de 500 milhões de execuções de testes por dia; ~60 000 submissões por dia.
- ~17 milhões de mutantes, ~760 000 alterações, ~2 milhões de mutantes apresentados (~12,5%) **[resumo]**.
- SBR é 68% dos mutantes; ROR é produtivo em 84,1% e UOI em 74,5% dos casos **[resumo]**.
- A heurística de logging: 99 em 100 nós amostrados correctamente marcados.
- Feedback explícito em só ~3,2% dos mutantes apresentados; ~82% destes considerados úteis **[resumo]**.

## Limites (ameaças à validade)
Código e cultura de revisão do Google (generalização incerta); heurísticas *unsound*; viés de selecção (só ~3,2% dão feedback); parte do ganho vem de **mostrar menos** e não só de escolher melhor; o score não é comparável com o tradicional; não há medida de bugs reais encontrados. Se mutantes se correlacionam com falhas reais: **não confirmado** nos trechos lidos.

## Para a nossa equipa (sugestões nossas)
- Mutar só as linhas alteradas de cada PR; limitar mutantes por linha/ficheiro.
- Excluir código de baixo valor (logging, timers, flags). No PIT: `avoidCallsTo`, exclusões (de memória, não verificado aqui), o que já aplicámos para `org.slf4j`.
- Não usar o score global como gate; avaliar só os sobreviventes do diff; recolher feedback "útil / não útil" e rever regras periodicamente.
