# Tratamento e modelagem de erros da API

## 1. Exceptions anotadas com @ResponseStatus

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) //, reason = "Entidade não encontrada")
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
    
}
```


## 2. Extends ResponseStatusException

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntidadeNaoEncontradaException extends ResponseStatusException {

    public EntidadeNaoEncontradaException(HttpStatus status, String mensagem) {
        super(status, mensagem);
    }

    public EntidadeNaoEncontradaException(String mensagem) {
        this(HttpStatus.NOT_FOUND, mensagem);
    }
    
}
```

## 3. Problema de granularidade de exception

Imaginemos que decidimos criar uma exception com o nome BusinessException a qual pretendemos usar em multiplos pontos da aplicação, por exemplo.  

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NegocioException extends RuntimeException {

    public NegocioException(String msg) {
        super(msg);
    }
    
}
```

###### --> Quais os problemas com esta abordagem?

* Esta exception é demasido genêrica. Não se consegue transmitir qualquer informação sobre o contexto do problema.
* Esta exception por ser muito genêrica, caracterisa-se tambem como tendo uma granularidade muito alta ou "grossa".
* Esta caracteristicas provocam possiveis problemas:
 
    - O cliente, pois este não será capaz de fazer um tratamento muito especifico da exception.
    - um futura evolução do software torna-se mais dificil, pois teremos mais dificuldade em entender o impacto das acterações.


###### --> Para evitar o problema anterior: 

* podemo criar Exceptions de granularidade mais baixa, "Fina".
* Serão exceptions com uma especificidade maior.

```java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountNotFoundException extends NegocioException {

    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
```

* com esta abordagem o cliente tem a possibilidade de tratar a exception de forma generica ou particular.
* No exemplo a anotação @ResponseStatus é facultativa na class AccountNotFoundException, pois o seu super já a declara.


###### --> Qual a melhor granularidade:
* podemos questionar nos se vale a pena ou não: 
    
    * quem vai usar o código, terá a necessidade de reagir de formas diferentes dependendo do motivo que levou a lançar a exception?
    
    * se a resposta for **Sim**, então a granularidade deve ser baixa.
    * se a resposta for **Não**, então a granularidade pode ser alta.



## 4. controlador com @ExceptionHandler

```java
@RestController
@RequestMapping(value = "/cidades")
public class CidadeController {

    @GetMapping
    public List<Cidade> listar() {
        return cidadeRepository.findAll();
    }

    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> tratarNegocioException(NegocioException e) {
        Problema problema = Problema
            .builder()
            .dataHora(LocalDateTime.now())
            .mensagem(e.getMessage())
            .build();
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(problema);
    }

}

@Getter
@Builder
public class Problema {
  private LocalDateTime dataHora;
  private String mensagem;
}
```

* desta forma consegue-se controlo absoluto sobre toda a resposta.
* como limitação temos que a exception handler é apenas aplicada no contexto do controlador currente.

## 5. Exceptions globais com `@ExceptionHandler` e `@ControllerAdvice`

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<?> tratarEntidadeNaoEncontradaException(
            EntidadeNaoEncontradaException e) {
        Problema problema = Problema.builder()
                .dataHora(LocalDateTime.now())
                .mensagem(e.getMessage()).build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problema);
    }
    
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> tratarNegocioException(NegocioException e) {
        Problema problema = Problema.builder()
                .dataHora(LocalDateTime.now())
                .mensagem(e.getMessage()).build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problema);
    }
    
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> tratarHttpMediaTypeNotSupportedException() {
        Problema problema = Problema.builder()
                .dataHora(LocalDateTime.now())
                .mensagem("O tipo de mídia não é aceite.").build();
        
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(problema);
    }
    
}

@Getter
@Builder
public class Problema {
    private LocalDateTime dataHora;
    private String mensagem;
}
```

## 6. Exception handler global com ResponseEntityExceptionHandler

* Existem algumas classes exceptions do spring os quais o container já faz um tratamento, então nós podemos pouparnos ao tratamento dessas exceptions, basta nos extender a classe `ResponseEntityExceptionHandler`.
* Uma das exceptions tratadas é por exemplo `HttpMediaTypeNotSupportedException`. Então podemos remover nossa implementação de handler dessa exception.
* Uma nota importante é que handler feitos dentro de `ResponseEntityExceptionHandler` não têm body. Se pretendermos que passem ter body teremos que costumizar.

```java
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<?> tratarEntidadeNaoEncontradaException(
            EntidadeNaoEncontradaException e) {
        Problema problema = Problema.builder()
                .dataHora(LocalDateTime.now())
                .mensagem(e.getMessage()).build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(problema);
    }
    
    @ExceptionHandler(NegocioException.class)
    public ResponseEntity<?> tratarNegocioException(NegocioException e) {
        Problema problema = Problema.builder()
                .dataHora(LocalDateTime.now())
                .mensagem(e.getMessage()).build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(problema);
    }
}

@Getter
@Builder
public class Problema {
    private LocalDateTime dataHora;
    private String mensagem;
}
```