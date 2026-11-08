# SE Radio 632: Goran Petrović sobre mutation testing no Google (podcast)

- **Fonte:** https://se-radio.net/2024/09/se-radio-632-goran-petrovic-on-mutation-testing-at-google/
- **Tipo:** podcast com transcrição automática
- **Leitura: QUASE COMPLETA.** Transcrição lida até ao fim; a parte final foi extraída por um modelo (fidelidade não garantida) e pode faltar um pequeno troço perto de 00:28–00:30. Ver "Aprofundamento".

## Em resumo
**Tópico:** filtrar para mostrar poucos mutantes e úteis (transcrição lida até ao fim).
- Os primeiros resultados eram dominados por mutantes inúteis, como apagar linhas de log.
- Filtros e feedback subiram a utilidade de ~12% para ~90%.
- Cuidado com testes que só repetem a implementação.
- Muitos números são estimativas do apresentador.

## Tese
Mutation testing integrado no code review e **filtrado para mostrar poucos mutantes úteis** mede se os testes apanhariam bugs plausíveis, e não apenas se as linhas são executadas.

## Pontos de maior interesse
- Cobertura não basta: um teste sem asserts pode dar 100% de cobertura.
- Os sobreviventes são os mais úteis: apontam asserts em falta.
- Os primeiros resultados eram dominados por mutantes inúteis (por exemplo apagar linhas de log). **Filtros heurísticos e feedback dos colegas** subiram a utilidade de cerca de 12% para cerca de 90%, segundo o relato.
- Para escalar: um mutante por linha, escolhido ao acaso; resultados no code review, na linha afectada, com sugestão de testes.
- Cuidado com **testes "detectores de mudança"** (que apenas repetem a implementação).
- Linguagens cobertas: C++, Python, Java, Go, TypeScript, JavaScript, Kotlin, Dart, Common Lisp, SQL e Rust.
- Mutação ao nível da AST, só nas linhas alteradas e cobertas de um PR.

## Números (segundo o apresentador)
93 code reviews (2016); cerca de 2500 utilizadores (2017); dezenas de milhares de engenheiros (2021). Estimativa do autor: 7 mil milhões de anos para uma iteração sobre 2 mil milhões de linhas sem limitar mutantes. Cerca de 69% dos bugs reais estavam ligados a mutantes que teriam sido apanhados.

## Limites
Muitos números são estimativas ou relatos anedóticos, sem metodologia; scores não são comparáveis entre ferramentas ou linguagens (palavras do autor); contexto Google (infraestrutura e cultura próprias).

## Para a nossa equipa (sugestões nossas)
Filtrar mutantes em logging e código sem testes; tratar sobreviventes como sugestões de asserts, não como metas a eliminar; vigiar testes que afirmam mensagens de excepção ou detalhes de implementação. Para Java, as notas indicam o Major e o resumo refere o PIT: não confirmado.

## Aprofundamento

**Cobertura da leitura (actualizada).** Transcrição lida em texto até ao fim (00:55:11). A parte desde ~00:27 foi lida numa segunda passagem, extraída por um modelo, por isso a fidelidade palavra a palavra não é garantida e pode faltar um pequeno troço à volta de 00:28–00:30. A transcrição é automática e tem erros ("Mutt Mutt" por Mutmut, "mega mutants" por meta-mutants). Os números de 2016–2021, "500 milhões de testes por dia" e "15 milhões de mutantes" são afirmações do apresentador, sem fonte verificada.

**Exemplos concretos do episódio.**
- *Útil:* numa função de soma, substituir o corpo por `return 0`. Se o mutante sobrevive, o teste não verifica nada.
- *Útil, fronteira:* idade `>= 18` contra `> 18`; um mutante vivo mostra que o valor de fronteira não está testado.
- *Inútil:* apagar uma linha de log; mudar um valor usado só em métricas, logging ou `sleep`; mutar *wrappers* de logging ou caches (equivalentes).
- *Detector de mudança:* afirmar a mensagem exacta de uma excepção em vez do seu tipo. Um bom conjunto de testes devia sobreviver a uma reimplementação completa.
- Eliminaram o mutante "negar literais numéricos" por ser o menos útil.

**Filtros e ciclo de feedback.** O primeiro protótipo gerou dezenas de milhares de mutantes e o autor deixou de os ver passadas três páginas. Os filtros foram codificados um a um a partir do feedback dos colegas ("útil / não útil"); a utilidade foi de ~12% para ~90%. São "milhares" de heurísticas em 11 linguagens, agrupadas em "cinco ou seis" famílias (por exemplo não mutar logging nem alterações de capacidade de colecções).

**Como evitam sobrecarregar o programador.**
- Só linhas alteradas e cobertas do PR; nenhum mutante em ficheiros alheios.
- Limite por ficheiro e por linha (historicamente "no máximo 10 ou 5") e um mutante por linha, escolhido ao acaso.
- A atenção do programador é o recurso mais escasso: as ferramentas são *opt-out*, e uma ferramenta ruidosa perde utilizadores em dois dias.
- Resultados no code review, na linha, com sugestão de testes; execução típica de poucos minutos; não corre em PRs automáticos nem em grandes limpezas automáticas.
- Escala: cache de compilação, cada mutante é uma pequena alteração sobre um artefacto já compilado.

**Conselhos para equipas pequenas.** Começar com a ferramenta da própria linguagem, integrada no CI existente, sem pedir "uma tarefa extra" ao programador; suprimir o máximo de mutantes possível; o objectivo é "fazer melhores produtos", não matar mutantes.

**Java.** O Google usa um *parser* de Java open source e muta ao nível da AST para mostrar o diff. PIT é citado como ferramenta Java (Mutmut para Python, Stryker para JavaScript). O episódio não dá configuração específica para Java/Spring.

**No nosso projecto.** "Detector de mudança" é o risco de testar o `jitteredBackoff` pelos valores exactos aleatórios: por isso os testes afirmam **invariantes** (banda, tecto, variação) quando há jitter, e valores exactos só com jitter 0.
