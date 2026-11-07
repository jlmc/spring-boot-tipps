# SE Radio 632: Goran Petrović sobre mutation testing no Google (podcast)

- **Fonte:** https://se-radio.net/2024/09/se-radio-632-goran-petrovic-on-mutation-testing-at-google/
- **Tipo:** podcast com transcrição automática
- **Leitura: PARCIAL.** Do texto, ~39 mil de ~75 mil caracteres lidos na íntegra (até cerca dos 00:27). O resto chegou como resumo gerado por modelo; os pontos marcados **[resumo]** assentam só nisso. A transcrição é gerada automaticamente; as notas do episódio são só links.

## Tese
Mutation testing integrado no code review e **filtrado para mostrar poucos mutantes úteis** mede se os testes apanhariam bugs plausíveis, e não apenas se as linhas são executadas.

## Pontos de maior interesse
- Cobertura não basta: um teste sem asserts pode dar 100% de cobertura.
- Os sobreviventes são os mais úteis: apontam asserts em falta.
- Os primeiros resultados eram dominados por mutantes inúteis (por exemplo apagar linhas de log). **Filtros heurísticos e feedback dos colegas** subiram a utilidade de cerca de 12% para cerca de 90%, segundo o relato.
- Para escalar: um mutante por linha, escolhido ao acaso; resultados no code review, na linha afectada, com sugestão de testes.
- Cuidado com **testes "detectores de mudança"** (que apenas repetem a implementação).
- Linguagens cobertas: C++, Python, Java, Go, TypeScript, JavaScript, Kotlin, Dart, Common Lisp, SQL e Rust.
- **[resumo]** Mutação ao nível da AST, só nas linhas alteradas e cobertas de um PR.

## Números (segundo o apresentador)
93 code reviews (2016); cerca de 2500 utilizadores (2017); dezenas de milhares de engenheiros (2021). Estimativa do autor: 7 mil milhões de anos para uma iteração sobre 2 mil milhões de linhas sem limitar mutantes. **[resumo]** Cerca de 69% dos bugs reais estavam ligados a mutantes que teriam sido apanhados.

## Limites
Muitos números são estimativas ou relatos anedóticos, sem metodologia; scores não são comparáveis entre ferramentas ou linguagens (palavras do autor); contexto Google (infraestrutura e cultura próprias).

## Para a nossa equipa (sugestões nossas)
Filtrar mutantes em logging e código sem testes; tratar sobreviventes como sugestões de asserts, não como metas a eliminar; vigiar testes que afirmam mensagens de excepção ou detalhes de implementação. Para Java, as notas indicam o Major e o resumo refere o PIT: não confirmado.
