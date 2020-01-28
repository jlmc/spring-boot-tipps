# HETEOAS

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

# Links úteis

- [HAL - Hypertext Application Language](http://stateless.co/hal_specification.html)

    - Especificação mais utilizada
    - Primeira especificação a ser adoptada pelo spring hateoas

- [UBER - Uniform Basis for Exchanging Representations](https://rawgit.com/uber-hypermedia/specification/master/uber-hypermedia.html)
    
- [JSON:API](https://jsonapi.org/)

- [JSON for Linking Data](https://json-ld.org/)

- [Collection+JSON](http://amundsen.com/media-types/collection/format/)

- [Siren](https://github.com/kevinswiber/siren)


# Como usar HETEOAS com a especificação HAL

1. Inicialmente temos a seguinte classe de representação, sem claquer Link do HETEOAS.

    ```java
    @ApiModel(value = "CookerOutput")
    @Data
    public class CookerOutputRepresentation {
    
        private Long id;
        private String title;
    
    }
    ```
   
   A serialização para json de uma instancia da classe anterior resultará no seguinte:
   
   ```json
   {
     "id": 123,
     "title": "Manuel Bras"
   }
   ```
   

2. Primeira coisa a fazer, é dizer que a nossa classe de representação tem suport a HETEOAS. Repare-se no package `org.springframework.hateoas.RepresentationModel` que é do `springframework.hateoas`.

    ```
    import org.springframework.hateoas.RepresentationModel;
    
    @ApiModel(value = "CookerOutput")
    @Data
    public class CookerOutputRepresentation extends RepresentationModel<CookerOutputRepresentation> {
    
        private Long id;
        private String title;
    
    }
    ```
   
   A representação de uma instancia da classe anterior sem que se faça nada sobre ele, isto é, sem que se adicionem os links, resultará exactamente na mesma representação do ponto 1.
   
3. é necessario então adicionar os Links a cada uma das instancias. A classe `org.springframework.hateoas.RepresentationModel` possui os metodos necessários.

    ```
    final CookerOutputRepresentation cookerOutput = repository.findById(cookerId)
                    .map(assembler::toRepresentation)
                    .orElseThrow(() -> CookerNotFoundException.of(cookerId));
    
    // https://www.iana.org/assignments/link-relations/link-relations.xhtml
    cookerOutput.add(new Link("http://example.local:8080/cookers/1"));
    cookerOutput.add(new Link("http://example.local:8080/cookers", IanaLinkRelations.COLLECTION));
    
    return cookerOutput;
    ```
   
   Agora a representação desta conterá os links do HETEOAS
   
   ```json
   {
       "id": 1,
       "title": "Mario Nabais",
       "_links": {
           "self": {
               "href": "http://api.exemple.local:8080/cookers/1"
           },
           "collection": {
               "href": "http://api.exemple.local:8080/cookers"
           }
       }
   }
   ```


4. Os links gerados deve sergir as "bests practices", então existe uma entidade que coordena o nome dos links essa entidade é a [IANA](https://www.iana.org/) e a documentação sobre este assunto pode ser encontrada em [IANA link-relations](https://www.iana.org/assignments/link-relations/link-relations.xhtml)

5. Reta nos fazer com que os links sejam gerados dinamicamente. Para isso podemos usar a classe `WebMvcLinkBuilder`

    ```
   
        final CookerOutputRepresentation cookerOutput = repository.findById(cookerId)
                .map(assembler::toRepresentation)
                .orElseThrow(() -> CookerNotFoundException.of(cookerId));

        final Link linkSelf = WebMvcLinkBuilder.linkTo(CookerResources.class)
                .slash(cookerOutput.getId())
                .withSelfRel();

        final Link linkCollection = WebMvcLinkBuilder.linkTo(CookerResources.class)
                .withRel(IanaLinkRelations.COLLECTION);

        cookerOutput.add(linkSelf);
        cookerOutput.add(linkCollection);

        return cookerOutput;
   ```
   
6. Podemos melhorar a geração dos links no codigo anterior:

    ```
           cookerOutput.add(linkTo(
                   methodOn(CookerResources.class)
                           .getById(cookerOutput.getId())).withSelfRel());
           cookerOutput.add(linkTo(
                   methodOn(CookerResources.class)
                           .list()).withRel(IanaLinkRelations.COLLECTION));
   ```


## 


```json
{
    "_embedded": {
        "restaurants": [
            {
                "takeAwayTax": 0,
                "active": false,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/restaurants/2"
                    }
                }
            },
            {
                "takeAwayTax": 0,
                "active": false,
                "_links": {
                    "self": {
                        "href": "http://localhost:8080/restaurants/1"
                    }
                }
            }
        ]
    }
}
```

