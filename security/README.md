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

### Custom Basic configuration

Podemos alterar as configurações default que o spring fornece da seguinte forma:

```java
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
 import org.springframework.security.config.http.SessionCreationPolicy;
 
 /**
  * Esta classe tem de extender a classe WebSecurityConfigurerAdapter, porque o objectivo é fazer override do metodo:
  * <p>
  * protected void configure(final HttpSecurity http) throws Exception {
  */
 @Configuration
 @EnableWebSecurity // enable the spring web security
 public class SecurityConfig extends WebSecurityConfigurerAdapter {
 
     @Override
     protected void configure(final HttpSecurity http) throws Exception {
         //super.configure(http);
 
         //@formatter:off
         http
             .httpBasic()
             .and()
             .authorizeRequests()
                 // define all free request '**' represent any thing
                 .antMatchers("/ping").permitAll()
                 // authorize any authenticated request
                 .anyRequest().authenticated()
             // disable session manager and cookies, because a rest api should be stateless
             .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
             // disable csrf
             .and().csrf().disable()
         ;
         //@formatter:on
     }
 }

```

NOTAS SOBRE A IMPLEMENTAÇÃO:

- A classe é uma `@Configuration` e que faz enable `@EnableWebSecurity` que por sua vez é tambem uma configuração, ou seja, a utilização da anotação  @Configuration é facultativa.
- É mandatorio extender a classe WebSecurityConfigurerAdapter, pois o objectivo é fazer Override do method `protected void configure(final HttpSecurity http) throws Exception`
- `.antMatchers("/ping").permitAll()` acesso livre de qualquer autenticação, o caracter `**` representa any path extra.
- `.anyRequest().authenticated()` todos os restantes acessos precisão de ser autenticados.
- `.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)` tornar a API Stateless, sem sessions ou cookies.
- `.and().csrf().disable()` disable csrf security control.


### Utilizadores em memória

```java

 @Configuration
 @EnableWebSecurity // enable the spring web security
 public class SecurityConfig extends WebSecurityConfigurerAdapter {
    

   @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        //super.configure(auth);

        final PasswordEncoder passwordEncoder = passwordEncoder();
        auth.inMemoryAuthentication()
                .withUser("john")
                    .password(passwordEncoder.encode("pwd"))
                    .roles("ADMIN", "CLIENT")
                .and()
                .withUser("foo")
                    .password(passwordEncoder.encode("pwd"))
                    .roles("CLIENT")
                ;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 }
```

NOTAS:

- Esta configuração é feita tambem na extenção da classe WebSecurityConfigurerAdapter
- É mandatório definir as roles `.roles("ADMIN", "CLIENT")`, caso contrario dá erro a iniciar, de momento pode ser qualquer valor valor.
- É mandatório usar um metodo de password encoder, por isso defini-se a bean `PasswordEncoder`


## OAuth2

- [Especificação do OAuth 2.0](https://tools.ietf.org/html/rfc6749)


##### Spring solutions

- [spring-security-oauth-2-0-roadmap-update](https://spring.io/blog/2019/11/14/spring-security-oauth-2-0-roadmap-update)

- Authorization server:
  - [spring-security-oauth](https://spring.io/projects/spring-security-oauth)
- Resource server:
  - [spring-security](https://spring.io/projects/spring-security)



#### Fluxos

##### Resource Owner Password Credentials Grant

Forma de obter um token, a partir de um username and password.
Este fluxo na literatura é tambem muitas vezes talbem denominado de outros nomes os quais todos significam e apontam para  mesma coisa:
    - Password Credencials
    - Password flow
    - Password Grant
    - Password Grant Type




1. Client solicita as credenciais ao utilizador (resource owner).
2. Com as credenciais do utilizador, o client faz um post ao authorization-server como forma de obter um token. Esse request normalmente possui as seguintes caracteristicas:
    - O client precisa de basic authentication no authorization-server
    - O POST é com `content-type x-www-form-undencoded`
    - O body do pedido possui uma property: `grant_type=password` e as credenciais do utilizador, ou seja:
        - `username=foo&password=1234&grant_type=password`
3. Se as credenciais estiverem correctas os authorization-server retorna um access token ao client embutido em um json object, ou seja:
    - ```json
        {
            "access_token": "abc-bl-bla",
            "token_type": "bearer",
            "expires_in": 999,
            "scope": "write read"
        }
      ```
      - o access token encontra-se na propriedade: `access_token`
      - `token_type` igual a `bearer` significa que que possui o currente acceass_token esta mandatado para ter acesso ao recursos em nome do resource-owner.
      - `expires_in` diz qual otempo de expiração em segundos. depois desses segundos o possuidor do token deixa de ter acesso aos recursos.
      - `scope` diz quais o scopes de acessos, podem ser os nomes que se pretende, não existe qualquer regra.
4. O client vai guardar o access_token, e enviar-lo ao resource-server em todas as Requisitions que lhe fizer. O client não deve guardar as credenciais do utilizador.
5. O Resource-Server em todas as Requisitions valida com o `Authentication-Server` (existe uma nova comunicação HTTP) se o access_token recebido ainda esta válido.
6. E em caso afirmativo, retorna então a representação do resource.

##### Caracteristicas da Applicação Cliente
- Deve ser uma applicação confiavel, não deve ser uma applicação de uma outra empresa por exemplo.
- Se a API é pública, e, entidades que podem ser desconhecidas vão usar esta API, então não devemos usar este FLOW.
- Idealmente este fluxo deve ser usado apenas para aplicações pertencentes a uma mesma organização.
- Este fluxo deve ser evitado, e apenas usado apenas em caso de extrema necessidade.