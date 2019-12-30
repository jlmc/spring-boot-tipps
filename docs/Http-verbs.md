# HTTP Verbs


Idempotência: É a propriedade que algumas operações têm de poderem ser aplicadas várias vezes sem que o valor do resultado se altere após a aplicação inicial. 
 
- [wiki - en](https://en.wikipedia.org/wiki/Idempotence) 
- [wiki - pt](https://pt.wikipedia.org/wiki/Idempot%C3%AAncia) 
 
 
### Idempotence Methods 
 
- O melhor exemplo é salvar um ficheiro varias vezes no qual apenas na primeira existia algo para salvar, apenas o primeiro save provoca um efeito colateral. 
 
- Quando o metodo não gerá qualquer efeito colateral e não modifica qualquer recuso é chamado de `Método Segudo` / `Safe Method` 
 
 
### NOT Idempotence Methods 
 
- Um bom exemplo pode ser, imaginemos que temos um recuso maça, no qual se vai dando dentadas, sucessivas, o recurso maça é alterado em cada dentada, ou seja, cada dentada provoca um efeito colateral.
 

### Safe Methods 

- Metodo que não faz qualquer operação de alteração, não é transacional, apenas de leitura. Um Safe method é tambem um Idempotence method  


--- 

## VERBS 
 
- GET:  
  - **Safe Method**  
  - **Idempotence** 
 
- POST:  
  - **Não Safe**,  
  - **Nao Idempotence** 
  - exemplo: Se com um mesmo payload invocarmos varias vezes a criação de um endpoint que tem a finalidade de criar um recurso, então vários recursos devem ser criados. 
  
- PUT: 
  - **Não Seguro** (Modifica os dados de recursos) 
  - **Idempotence**, porque se executarmos várias vezes sobre um mesmo recuso o resultado será sempre o mesmo primeira requisição. 
  - O PUT pode ser usado como create or update, embora a maioria dos developers usam no apenas para atualizar. 
 
- PATH:  
  - **NOT Safe** (Modifica os dados do recurso) 
  - **Idempotence**, porque se executarmos várias vezes sobre um mesmo recuso o resultado será sempre o mesmo primeira requisição. 
  - Serve Para atualizar um recurso parcialmente 
  - Implementação mais complexa. 
  
- DELETE: 
  - **Not Safe** (Modifica os dados do recurso) 
  - **Idempotence**, porque se executarmos várias vezes sobre um mesmo recuso o resultado será sempre o mesmo primeira requisição. 
  - Verbo usado para remover recursos. 
  - Alguns developers questionam se o Delete devia ser Idempotente, nada mais errado, porque o que importa refletir é sobre o efeito colateral da aplicação e como o recurso só pode ser removido na primera vez, as restantes chamadas não provocam alteração dos dados, independentemente do resultado ou status code da requisition. Por esta razão o method é Idempotente. 
  
- HEAD: 
  - **Safe Method**  
  - **Idempotence** 
  - Igual ao GET, mas sem qualquer body na resposta. 
  - Usado apenas obter um header, às vezes um consumidor não esta interessado no body da response,  
  - por exemplo:  
  
    1. Saber se um URI existe mesmo 
    2. Saber se um ContentType é aceite pelo servidor 
    3. etc. 
 
- OPTIONS: 
  - **Safe Method**  
  - **Idempotence** 
  - Usado para Obter a lista de methods suportados por um recurso 
  - Regra geral nem todos os recursos necessitam de todos os vebs, então é útil ao consumidor saber quais os vebs disponíveis. 
  
    ``` 
    OPTIONS /cookers/11 HTTP/1.1 
      
    HTTP/1.1 200 OK 
    Allow: GET, PUT, DELETE 
    ```
    
-- 

# Artigos que podem ajudar a escolher qual o verbo

-  http://restcookbook.com/Basics/loggingin/