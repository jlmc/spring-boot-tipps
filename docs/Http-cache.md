# Http Cache

"A requisição mais rapida que existe é aquela que nem chega a ser realizada"

Origin Server = Api Server

## Tipos de Caches

- Local
    - O browser pode fazer cache de response a pedidos localmente
- Server Proxy
    - Servidor intermediário entre a API e o client

## Vantagens

- Reduz o numero de bytes na rede, o consumo de rede é mais baixo
- Reduz lâtencia, importante para clientes moveis, por exemplo.
- Reduz carga no servidor da API
- Esconde problemas na rede, Por exemplo se o Origin Server cair por um periodo, recursos que se encontram em cache continuam disponiveis.

## Atenções

- Não se deve fazer caches em recursos quandos os clientes não suportam/toleram aterações no recurso
- Quandos os dados são alterados com uma frequencia muito alta.



## Habilitar o cache com o Http Header `Cache-Control` e a diretiva `max-age`


```java
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/payment-methods")
public class PaymentMethodResources {

    @Autowired
    PaymentMethodRepository repository;

    @Autowired
    PaymentMethodRegistrationService paymentMethodRegistrationService;

    @Autowired
    PaymentMethodInputRepresentationDisassembler disassembler;

    @Autowired
    PaymentMethodOutputRepresentationAssembler assembler;

    @GetMapping
    public ResponseEntity<List<PaymentMethodOutputRepresentation>> list() {
        final List<PaymentMethod> paymentMethods = repository.findAll(Sort.by("id"));
        final List<PaymentMethodOutputRepresentation> paymentMethodOutputRepresentations = assembler.toListOfRepresentations(paymentMethods);

        return ResponseEntity.ok()
                //header Cache-Control: max-age=10 segundo
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                .body(paymentMethodOutputRepresentations);
    }
}
```

- Apenas adicionando o `Header Cache-Control: max-age=10` será suficiente para que o browser faça cache desta resposta.



## ETags Validações

Vamos considerar o seguinte pedido e resposta:

1. GET /payments-methods/1

2. Resposta
```
HTTP/1.1 200
Cache-Control: max-age=10
ETag: "6198c0b33"

{
    "id": 1,
    "descripion": "credit card"   
}
```

- ETag Significa Entity Tag, é um código único calculado pelo servidor, que identifica a representação que se retornou.
- Para o Consumidor não importa a forma de como essa Tag foi gerada, o que importa é que é um código unico, e qualquer alteração na representação terá como consequência o calculo de uma Tag diferente.
- O Cliente na sua cache local irá fazer cache desta resposta durante 10 segundos, nessses dez segundos esta resposta é considerada 'Fresh'. O browser tendo a ETag na resposta guarda tambem esse código na mesma cache.

###### Mas a diferença é o que acontece após os 10 segundos:

Quando o registo na cache passar a 'Stale' (após os 10 segundos), o cliente terá a necessidade de fazer um novo pedido mas desta vez o pedido será:


1. Pedido
```
GET /payments-methods/1
If-None-Match: "6198c0b33"
```

- Com este pedido o que o cliente esta a pedir pode ser interpretado como:

    - Servidor, e tenho aqui uma representação do `/payments-methods/1` com a ETag = "6198c0b33", 
    - Caso a representação que existe no servidor ainda tenha a mesma Tag não é necessarios retorna nada. 304 NOT MODIFIED
    - Atenção que os Browsers escondem este 304 status code (mostram no como se fosse um 200). Mas para provar o comportamento podemos usar a ferramenta [wireshark](https://www.wireshark.org/)
    - Se a Representação no servidor tiver uma Tag diferente então deve ser retornada a nova representação.
    
    - Com isto o registo na cache local no cliente passa novamente a 'Fresh'.
    

2. Resposta
```
HTTP/1.1 304
Cache-Control: max-age=10
ETag: "6198c0b33"
```


Desvantagens/Vantagens:

- O servidor Origin acaba por ter de fazer processamento maior.

- Cansegue-se diminuir o consumo da network, existirá um menor volume de dados a circular na rede.


### Shallow ETags

- O Spring possui um filtro o qual tem como objectivo interceptar a responses e calcular a Hash, desta forma o controladores não ficam poluidos com o código do ETags.


```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * The web configuration class enables the CORS in all the request
 *
 * Here in this class we can add Servlet filters to the applications
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        // any request for the path mapping
        registry.addMapping("/**")
            //.allowedOrigins("http://foo.com")
            .allowedOrigins("*")
            //.allowedMethods("GET", "POST", "HEAD")
            .allowedMethods("*")
            .maxAge(1800)
            .allowCredentials(true)
        ;
    }

    @Bean
    public Filter shallowETagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }
}
```



## outras diretivas de Cache-Control na resposta HTTP

- HTTP Response Header  Cache-Control: max-age=10 private
    
    -  A keywork `private` especifica que é apenas valida para caches locais, não para servidores intermedios
    
- HTTP Response Header  Cache-Control: max-age=10 public
    
    -  A keywork `public` especifica que é apenas valida para caches locais e intermidiarios, esta é o comportamento default
    
- HTTP Response Header  Cache-Control: max-age=10 nocache

    - A keywork `no-cache` especifica que é necessario fazer sempre uma requisição para validar mesmo que estejamos no periodo Fresh
    
- HTTP Response Header  Cache-Control: noStore

    - A keywork `no-store` significa que não é permitiva cache
    
```java
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS))
                //.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePrivate())
                //.cacheControl(CacheControl.maxAge(10, TimeUnit.SECONDS).cachePublic())
                //.cacheControl(CacheControl.noCache())
                //.cacheControl(CacheControl.noStore())
                .body(paymentMethodOutputRepresentation);
```

## Pedidos que ignorem o cache

Por vezes precisamos de ignorar o valor da cache, isto é, queremos fazer um pedido que realmente vá ao Origin Server, independentemente de existir uma Fresh cache response. Para tal podemos simplemente adicionar ao Pedido o Header `Cache-Control: no-cache`

1. Pedido
```
GET /payments-methods/1
Cache-Control: no-cache
```