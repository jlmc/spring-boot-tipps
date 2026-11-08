# Practical Mutation Testing at Scale: A view from Google (Petrović, Ivanković, Fraser, Just; IEEE TSE 2021)

- **Fontes:** https://homes.cs.washington.edu/~rjust/publ/practical_mutation_testing_tse_2021.pdf · versão lida: https://ar5iv.labs.arxiv.org/html/2102.11378
- **Tipo:** paper industrial (follow-up do de 2018)
- **Leitura: QUASE COMPLETA** (~90%, versão arXiv/ar5iv; ver "Aprofundamento"). Por ler: o fim do apêndice A e os apêndices B–E. Os PDFs não ficaram legíveis.

## Em resumo
**Tópico:** como a mutação se tornou útil no Google (leitura quase completa).
- Mais de 100 regras manuais suprimem mutantes inúteis (logging, timeouts, flags).
- Teto de 7 mutantes × nº de ficheiros do changelist; botões "Please fix" / "Not useful".
- A produtividade dos mutantes apresentados passou de ~80% para ~89% com as supressões.
- Ameaças: viés de selecção (só ~3,2% dão feedback), contexto Google, sem medida de bugs reais.

## Tese
Mutation testing é viável à escala industrial se se limitar às **linhas alteradas e cobertas**, se **filtrar mutantes que os programadores acham improdutivos** e se mostrar **poucos sobreviventes** no code review.

## Pontos de maior interesse
- **Baseado em diffs:** só se mutam linhas alteradas e cobertas por testes.
- **Nós "áridos":** nós da AST sem valor (logging, reservas de memória, timeouts, flags) são marcados antes e nunca geram mutantes. Um nó composto só é árido se todos os filhos o forem.
- Mais de **100 regras manuais**, alimentadas por feedback dos programadores. Algumas são *unsound* (podem suprimir mutantes úteis) e os autores aceitam esse compromisso.
- **Operadores:** AOR, LCR, ROR, UOI, SBR. O ABS foi excluído por gerar sobretudo mutantes improdutivos.
- **Selecção:** um mutante por linha, com operadores em ordem aleatória; **teto de 7 vezes o nº de ficheiros do changelist**, escolhidos entre os sobreviventes.
- Mostrados como *findings* no code review, com botões de um clique ("Please fix" / "Not useful").
- **Produtividade:** subiu de ~80% para ~89% com as supressões (ver a correcção em "Aprofundamento").
- Um mutante apresentado **não é um bug**: a medida é o juízo do programador sobre utilidade.

## Números
- Escala: ~2 mil milhões de linhas; mais de 500 milhões de execuções de testes por dia; ~60 000 submissões por dia.
- ~17 milhões de mutantes, ~760 000 alterações, ~2 milhões de mutantes apresentados (~12,5%).
- SBR é 68% dos mutantes; ROR é produtivo em 84,1% e UOI em 74,5% dos casos.
- A heurística de logging: 99 em 100 nós amostrados correctamente marcados.
- Feedback explícito em só ~3,2% dos mutantes apresentados; ~82% destes considerados úteis.

## Limites (ameaças à validade)
Código e cultura de revisão do Google (generalização incerta); heurísticas *unsound*; viés de selecção (só ~3,2% dão feedback); parte do ganho vem de **mostrar menos** e não só de escolher melhor; o score não é comparável com o tradicional; não há medida de bugs reais encontrados. Se mutantes se correlacionam com falhas reais: **não confirmado** nos trechos lidos.

## Para a nossa equipa (sugestões nossas)
- Mutar só as linhas alteradas de cada PR; limitar mutantes por linha/ficheiro.
- Excluir código de baixo valor (logging, timers, flags). No PIT: `avoidCallsTo`, exclusões (de memória, não verificado aqui), o que já aplicámos para `org.slf4j`.
- Não usar o score global como gate; avaliar só os sobreviventes do diff; recolher feedback "útil / não útil" e rever regras periodicamente.

## Aprofundamento

**Cobertura da leitura (actualizada).** Lido em texto original da versão arXiv (ar5iv) da introdução até às conclusões e ao Apêndice A (cerca de 118 000 de 131 000 caracteres). Por ler: o fim do apêndice A e os apêndices B a E (JavaScript, Java, Python, Go). Os números antes marcados [resumo] foram **confirmados** no texto original. Atenção: a versão arXiv pode diferir da publicada na TSE.

**Correcção a esta ficha.** A produtividade dos mutantes apresentados subiu de **~80% para ~89%** com as supressões (texto original). O valor "~15% → ~89%" que aparecia antes vinha de um resumo e **não foi confirmado**.

**Nós áridos, com exemplos do paper.** Um nó da AST é árido se mutá-lo gera mutantes equivalentes ou improdutivos. Um nó simples segue uma regra de especialista; um nó composto só é árido se **todos** os filhos o forem. Há mais de 100 regras, escritas à mão a partir dos "Not useful". Exemplos de categorias:
- logging (`LOG(INFO) << ...`, `console.log`);
- capacidade de memória (`reserve`, `resize`, `new ArrayList` com tamanho inicial);
- contadores de monitorização;
- tempo (`SleepFor`, `set_deadline`);
- debug (`assert`, `TRACE`, `printStackTrace`);
- flags (`DEFINE_integer`);
- baixo nível (`flush`, `close`, `os.rename`);
- lookups de cache;
- `equals`/`hashCode`/`toString` em Java; `__name__ == '__main__'` em Python.

A regra de prefixo "log" foi validada numa amostra de 100 nós: 99 correctos. Várias regras são declaradas *not sound* (flush/close, boilerplate Java, early exit, version checks de Python): podem suprimir mutantes úteis.

**Operadores** (Tabela I; os exemplos de código são **nossos**, não do paper):

| Operador | Exemplo |
|---|---|
| AOR (aritmético) | `preco + taxa` → `preco - taxa` |
| LCR (lógico) | `if (a && b)` → `if (a \|\| b)` ou `if (true)` |
| ROR (relacional) | `if (n > 0)` → `if (n >= 0)` |
| UOI (inserção unária) | `i` → `i++`; `activo` → `!activo` |
| SBR (remover instrução) | `cache.put(k, v);` → bloco vazio |

O ABS foi excluído por gerar sobretudo mutantes improdutivos.

**Selecção (secção IV).**
1. Só linhas alteradas e cobertas, depois de retirar os nós áridos.
2. Operadores em ordem aleatória; o primeiro que gera mutante na linha ganha: **um mutante por linha**.
3. Teto de **7 vezes o número de ficheiros do changelist**, escolhidos ao acaso entre os sobreviventes. *(Corrige a ficha anterior, que dizia "7 por ficheiro".)*
4. Variante "targeted": ordena os operadores pelo histórico de sobrevivência/produtividade em contextos AST semelhantes (similaridade Jaccard, fingerprints MinHash). O "probabilístico" refere-se à escolha de operadores; o paper não define um limiar de supressão.

**Perguntas de investigação e respostas.**
- **RQ1:** mediana de mutantes por changelist: 820 sem supressão → 77 (um por linha) → **7** (árido + um por linha).
- **RQ2:** 87,5% dos mutantes são mortos; UOI sobrevive em 9,5% e LCR em 16,3%. Dart tem o valor mais alto, JavaScript o mais baixo.
- **RQ3:** ROR é produtivo em 84,1% dos casos, UOI em 74,5%; Java 87,2%, Python 70,6%.
- **RQ4:** usar contexto aumenta a sobrevivência em mais de 40% e os pedidos "Please fix" em quase 50%.

**Reacções.** Feedback explícito em 66 798 dos 2 110 489 mutantes apresentados (~3,2%); desses, 82% foram produtivos. O paper afirma que mostrar muitos mutantes não accionáveis levaria quase de certeza o programador a abandonar a ferramenta.

**Acoplamento com falhas reais: não medido.** O paper diz que sobreviver, por si só, não mede produtividade, e usa o feedback dos programadores. Correcção: o paper diz que o código Python precisa de mais testes por falta de compilador, e admite outras causas possíveis; a nossa frase "Python produz mais ruído" era imprecisa.

**No nosso projecto.** O `avoidCallsTo org.slf4j` é a nossa versão mínima das regras de logging. A regra do tecto de mutantes por PR é a que falta para a estratégia "incremental em PR" (roadmap §10 do guia).
