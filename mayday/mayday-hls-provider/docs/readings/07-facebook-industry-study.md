# What It Would Take to Use Mutation Testing in Industry: A Study at Facebook (Beller et al., 2021)

- **Fontes:** https://arxiv.org/abs/2010.13464 · texto lido: https://arxiv.org/html/2010.13464v3 (o PDF não foi legível)
- **Tipo:** paper industrial com estudo qualitativo
- **Leitura: COMPLETA** (corpo inteiro da versão HTML, do início às referências; ver "Aprofundamento"). Por ler: as Figuras 1, 2, 3 e 5 (imagens). O PDF não ficou legível.

## Em resumo
**Tópico:** mutadores aprendidos de bugs reais (leitura completa).
- Aprende operadores a partir de bugs de produção; 19 padrões.
- Só os sobreviventes vão para o code review.
- 84,6% dos engenheiros concordaram que expunha falta de testes; 46,2% criariam um teste.
- Amostra de 26, sem grupo de controlo, e o texto tem inconsistências nos números.

## Tese
Aprender operadores de mutação a partir de **bugs reais** e de alterações que causaram incidentes torna a mutação viável à escala industrial e produz lacunas de teste **accionáveis**.

## Pontos de maior interesse
- **Problema:** a mutação clássica gera mutantes em excesso, muitos mortos trivialmente ou sem valor para o programador.
- **Abordagem (Mutation Monkey):** usa o Getafix para aprender padrões "antes/depois" na AST a partir do Defects4J, de crashes de produção e de diffs que mudaram o resultado de testes. 19 padrões escolhidos por especialistas; "semi-automática", porque exige ajustar hiperparâmetros e converter manualmente em templates.
- **Selecção diária:** 100 diffs aleatórios que tocam Java; aplica-se o primeiro operador que encaixa.
- Os mutantes vão como diffs para o **code review**, passam pelo pipeline normal de testes e **só os sobreviventes são mostrados**; correm fora das horas de pico.
- **Estudo com engenheiros:** 29 abordados, 26 responderam; entrevistas semi-estruturadas.
- **Resultados:** 84,6% concordaram que o diff expunha falta de testes; 46,2% criariam ou adaptariam um teste. Razões para não agir: código prestes a ser descontinuado, código novo e instável, falta de tempo.
- UX: o log de visitas confundia e devia estar escondido.

## Números
15 493 mutantes em dois meses; 2060 testes distintos; mais de 3,5 milhões de visitas. Taxa de morte por operador: 30,6% a 53,1%. Concordância entre codificadores: κ = 0,802. O texto tem **inconsistências** (16, 17 e 19 operadores; 42,3% vs 46,2%).

## Limites
Um só código (app móvel em Java, muitos testes de UI); sem grupo de controlo; amostra de 26; os autores entrevistaram e codificaram os próprios resultados; sobreviver não prova lacuna (testes instáveis, código a remover); sem validação contra falhas reais.

## Para a nossa equipa (sugestões nossas)
- Limitar o PIT aos diffs do PR; tratar sobreviventes como **sugestões**, não como bloqueio.
- Criar mutadores próprios a partir de reverts e bugs da equipa, em vez de operadores genéricos.
- Correr em CI nocturno com limite de mutantes; medir quantos sobreviventes geram um teste, para avaliar utilidade.

## Aprofundamento

**Cobertura da leitura (actualizada).** Lido em texto original o corpo completo de https://arxiv.org/html/2010.13464v3, do início às referências. Por ler: as Figuras 1, 2, 3 e 5 (imagens). O PDF continua ilegível. As marcações **(resumo)** da ficha deixam de ser necessárias.

**Mutation Monkey, passo a passo.**
1. **Treino:** o Getafix recebe pares de árvores (antes/depois) e aprende padrões. Dados: 516 pares do Defects4J (438 correcções), mais de 18 000 correcções de *crashes* de produção (usadas invertidas) e 354 pares de diffs que mudaram o resultado de testes (179 de falha para passagem, 175 de passagem para falha).
2. **Selecção:** 19 padrões, escolhidos com avaliação de especialistas. "Semi-automático" porque exige afinar o Getafix e converter manualmente em templates (por exemplo o *wrapper* de logging).
3. **Aplicação diária:** de todos os diffs do dia escolhem-se 100 ao acaso; filtram-se os que não tocam Java; tentam-se os operadores por ordem até um encaixar.
4. Verificação de sintaxe antes de submeter.
5. Submissão como diff no code review, com o mesmo pipeline de testes dos diffs normais.
6. Só os mutantes **não mortos** são mostrados ao autor; execução fora de pico (19h–7h, hora do Pacífico).

**Exemplos de padrões** (escritos por nós, inspirados na Tabela I e na Figura 4):
- `REMOVE_NULL_CHECK` (34,9% de morte, 808 aplicações): `if (user == null) { return; }` seguido de `render(user);` fica só `render(user);`. Se nenhum teste falhar, falta o caminho nulo.
- `FLIP_TRUE_FALSE` (36,9% de morte): `if (isEnabled == true)` passa a `== false`.
- Exemplo da Figura 1 do paper: a condição `changes.isEmpty()` é substituída por `true`.

**Diffs comentados pelos engenheiros (texto original).** P4: num mutante em código de logging, a mudança só alterava o número enviado ao backend de logging; P9: adicionar testes cedo daria muitas falhas em código em mudança; P25: o código ia ser descontinuado.

**Respostas por tema (Tabela III, n = 26).**

| Tema | Resultado |
|---|---|
| O diff expõe falta de teste | 84,6% (22); 7,7% em desacordo; 7,7% indefinido |
| Diffs úteis | 61,5% (16); 0 em desacordo; 38,4% indefinido |
| Vão adicionar teste | 46,2% (12); 23,1% em desacordo; 30,8% indefinido |
| Informação de cobertura nova | 26,9% (7) |

Quase 85% acharam a ferramenta útil; a maioria nunca ouvira falar de mutation testing. O texto diz 11 (42,3%) em vez de 12 (46,2%) para "vão adicionar teste": divergência do próprio artigo. Três engenheiros já tinham submetido testes antes da entrevista.

**Razões para não escrever o teste.** Querer que o autor do diff escreva o teste; código de importância menor (logging; o filtro de logging não bastou); código a descontinuar; código novo e instável; falta de tempo; área com testes fracos em geral.

**Recomendações dos autores.** Pôr mutantes em canário para medir impacto real; incluir o logging no corpo do método/classe para encontrar testes próximos; incluir contexto externo (descontinuação, área sem testes); esconder o log na interface; filtrar logging com dataflow/AST; usar benchmarks abertos para comparar operadores.

**Limites.** 15 493 mutantes (contra 322 972 em Brown et al. e 1,1 milhões no Google; os autores argumentam que a comparação muda ao olhar para diffs e para compilação, 100% contra 12–15%); 26 engenheiros, sem grupo de controlo; os autores codificaram as entrevistas (refeito meio ano depois, κ = 0,802); sobreviver é "evidência, não prova".

**Correcções a esta ficha.** A sobrevivência "60–70%" vem da introdução; pela Tabela I (morte 30,6–53,1%) fica entre ~47% e ~69%. Os 16, 17 e 19 operadores aparecem em partes diferentes do texto.

**No nosso projecto.** O `REMOVE_NULL_CHECK` é o tipo de mutante que o PIT cobre com o `RemoveConditional` e que nos mostrou `NO_COVERAGE` em `BearerAccountResolver`/`GlobalExceptionHandler`. A ideia de "mutadores a partir de bugs próprios" ficaria para um passo futuro do roadmap.
