# Spring security

Para começar com a segurança do Spring, precisamos adicionar a dependência do maven:
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

Após adicionar apenas essa dependência, todas as solicitações que fazemos à aplicação precisarão ser autenticadas, por exemplo:

```
GET /users/1 HTTP/1.1

{
    "timestamp": "2020-02-22T22:15:00.984+0000",
    "status": 401,
    "error": "Unauthorized",
    "message": "Unauthorized",
    "path": "/users/1"
}
```




## Basic Authentication


Apenas adicionado a dependencia e sem fazer qualquer configuração este será um tipo de autenticação usado por defeito.

- O Spring security quando a app inicia, faz log da password gerada por defeito:
    `Using generated security password: 0ef8498b-6181-42c2-9975-144589ef4df7` 
- O Username de defeito é: `user`


- Então se fizer mos o pedido com essa informação obtemos uma resposta de sucesso:

```
GET /users/1 HTTP/1.1
Host: localhost:8080
Authorization: Basic dXNlcjowZWY4NDk4Yi02MTgxLTQyYzItOTk3NS0xNDQ1ODllZjRkZjc=
```

- Podemos alterar as configurações padrão com as seguintes application properties:

```
spring.security.user.name=test
spring.security.user.password=test
```



