# Restfull 

## TestRestTemplate

* Oferece metodos para o consumo de cenarios comuns atraves do metodo HTTP.

* Tolerante a falhas

* Se o Apache Http Client 4.3.2 ou superior estiver disponivel, ele será usado como cliente e será configurado para ignorar cookies e redireccionamentos.

## exchange Method

O método exchange é um método bastante generico, com diversas assinaturas e que pode executar qualquer método HTTP. No geral o que ele faz é executar um requisition e encapsula a resposta em uma instancia de ResponseEntity.


## getForEntity Method

* realiza chamada para o endereço, e converte o resultado para o tipo passado como parametro encapsulado em ResponseEntity

EG: 

```java
ResponseEntity<Task> response = testRestTemplate.getForEntity("/tasks/1", Task.class );
```

## getForObject Method

* realiza chamada para o endereço, e converte o resultado para o tipo passado como parametro.

```java
Task task = testRestTemplate.getForObject("/tasks/1", Task.class );
```

