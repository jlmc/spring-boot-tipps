# What It Would Take to Use Mutation Testing in Industry: A Study at Facebook (Beller et al., 2021)

- **Fontes:** https://arxiv.org/abs/2010.13464 · texto lido: https://arxiv.org/html/2010.13464v3 (o PDF não foi legível)
- **Tipo:** paper industrial com estudo qualitativo
- **Leitura: PARCIAL.** Lidos na íntegra só os primeiros ~39 000 de ~68 000 caracteres (introdução, contexto, Mutation Monkey, avaliação quantitativa, início do estudo com engenheiros). O resto veio como resumo de um modelo, não verificado: o que está marcado **(resumo)** vem daí. Mutantes equivalentes: sem tratamento formal, só a afirmação dos autores de que os padrões são "quase sempre não equivalentes por construção".

## Tese
Aprender operadores de mutação a partir de **bugs reais** e de alterações que causaram incidentes torna a mutação viável à escala industrial e produz lacunas de teste **accionáveis**.

## Pontos de maior interesse
- **Problema:** a mutação clássica gera mutantes em excesso, muitos mortos trivialmente ou sem valor para o programador.
- **Abordagem (Mutation Monkey):** usa o Getafix para aprender padrões "antes/depois" na AST a partir do Defects4J, de crashes de produção e de diffs que mudaram o resultado de testes. 19 padrões escolhidos por especialistas; "semi-automática", porque exige ajustar hiperparâmetros e converter manualmente em templates.
- **Selecção diária:** 100 diffs aleatórios que tocam Java; aplica-se o primeiro operador que encaixa.
- Os mutantes vão como diffs para o **code review**, passam pelo pipeline normal de testes e **só os sobreviventes são mostrados**; correm fora das horas de pico.
- **Estudo com engenheiros:** 29 abordados, 26 responderam; entrevistas semi-estruturadas.
- **Resultados (resumo):** 84,6% concordaram que o diff expunha falta de testes; 46,2% criariam ou adaptariam um teste. Razões para não agir: código prestes a ser descontinuado, código novo e instável, falta de tempo.
- UX: o log de visitas confundia e devia estar escondido.

## Números
15 493 mutantes em dois meses; 2060 testes distintos; mais de 3,5 milhões de visitas. Taxa de morte por operador: 30,6% a 53,1%. Concordância entre codificadores: κ = 0,802. O texto tem **inconsistências** (16, 17 e 19 operadores; 42,3% vs 46,2%).

## Limites
Um só código (app móvel em Java, muitos testes de UI); sem grupo de controlo; amostra de 26; os autores entrevistaram e codificaram os próprios resultados; sobreviver não prova lacuna (testes instáveis, código a remover); sem validação contra falhas reais.

## Para a nossa equipa (sugestões nossas)
- Limitar o PIT aos diffs do PR; tratar sobreviventes como **sugestões**, não como bloqueio.
- Criar mutadores próprios a partir de reverts e bugs da equipa, em vez de operadores genéricos.
- Correr em CI nocturno com limite de mutantes; medir quantos sobreviventes geram um teste, para avaliar utilidade.
