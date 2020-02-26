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

---

# Fluxos

## Resource Owner Password Credentials Grant

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



## Client Credentials Flow

- O access token gerado pelo authentication server não é uma delegação de acesso. 
- Ele representa uma chave de acesso em nome do próprio cliente.
- Com este flow não existe a possibilidade de usar refress_token.

1. Aplicação client, requista access_token
   ```http request
   POST /oauth/token
   Content-Type: application/x-www-form-unlencoded
   Authentication: Basic clientId:clientSecret
   
   grant_type=client_credentials
   ```
2. O Authorization-Server se as informações estiverem correctas responde com o token em um json object
    Status: 200
    ```json
    {
       "access_token": "abc-bl-bla",
       "token_type": "bearer",
       "expires_in": 999,
       "scope": "write read"
    }
   ```
3. A aplicação client envia o access_token em todas as requisições que fizer para o Resource-Server
5. O Resource-Server em todas as Requisitions valida com o `Authentication-Server` (existe uma nova comunicação HTTP) se o access_token recebido ainda esta válido.
6. E em caso afirmativo, retorna então a representação do resource.




##### Caracteristicas da Applicação Cliente
- Aplicação sem qualquer interação com utilizador
- Aplicação puramente backend
- pretende realisar operações em seu nome proprio, e não em nome de uma resource owner
- Executa por exemplo tarefas agendadas



## Authentication Code Flow

Denominado tambem como fluxo básico Oauth.

![Authentication Code Flow diagram](diagramas/authentication-code-flow.png)

1. **Resource Owner** (user) faz um acesso a uma aplicação **Client**, por exemplo um web site.

2. A Aplicação **Client** precisa de ter acesso aos recursos do **Resource Server**, e para que isso seja possível é necessário que possua um **access_token**. No entanto, ele não não tem ainda esse **access_token**.
    - A invês do Client pedir o **username/password** ao **Resource Owner** (utilizador), O Client solicita uma autorização ao Autorization-Server.
    - Faz um redirecionamento, um link por exemplo, que segue o padrão apresentado a seguir
        ```
        REDIRECT:
        https://AUTHORIZATION-SERVER/oauth/authorize?response_type=code&client_id=CLIENT_ID&state=ABC9876&redirect_uri=https//CLIENT-APP/XYZ
        ```
        - response_type: o client esta a solicitar um codigo.
        - client_id: o client registado no Authorization-Server
        - state: uma string qualquer, deve ser aleatória (random), e que basicamente serve para prevenir ataques CSRF.
        - redirect_uri: O Client informa o Authorization-Server que depois da autorização ser concedida, volta (faz um redirect) para o uri. esta URI, não pode ser qualquer URI, ela precisa estar registada no Authorization-Server.
3. O **Authorization-Server** vai solicitar ao **Resource-Owner**, para autorizar  acesso a uma determinada aplicação aos seus recursos. 
    - Caso não esteja Autenticado, pedirá para fazer login.
    - Quando/Após Autenticado, perguntará se pretende autorizar a aplicação **Client** aos seus recursos.
    
4. Se o Resource-Owner autorizar acesso, o Auhorization-Server gera um código de acesso e envia para o Client (atravês de redirect). Este codigo não é ainda o access_token.

    ```
   REDIRECT:
   https//CLIENT-APP/XYZ?code=abcd1234&state=ABC9876
   ```

5. O Client pode então solicitar um access_token ao Authentication-Server

    ```http request
   POST /oauth/token
   
   Content-Type: application/x-www-form-unlencoded
   Authentication: Basic client-id/client-secret
   
   code=abcd1234&grant_type=authorization_code
   ``` 

6. O Authorization-Server Para gerar o access_token precisa tambem de invalidar o code, pois o code pode apenas ser utilizado uma vez. como resposta o Authorization-Server response com o Json Object 

        Status: 200
        Content-Type: application/json
        
        ```json
        {
           "access_token": "abc-bl-bla",
           "token_type": "bearer",
           "expires_in": 999,
           "scope": "write read",
           "refress_token": "1234-abc"
        }
       ```


##### Caracteristicas da Applicação Cliente
- O client permite authentication com redes sociais (por exemplo).


##### Como usar

1. No browser, fazer o pedido de authorization_code
`http://localhost:8081/oauth/authorize?response_type=code&client_id=food4u-analytics-client-id&state=ABC&redirect_uri=http//food4u-analytics-client`


## Authentication Implicit Grant Flow

- É um fluxo semelhante, ao anterior (Authentication Code Flow).
- É no entanto mais simple e menos segudo, porque o access_token é retornado logo no primeiro request na URL, e por isso, a sua utilização é fortemente desencorajada.
- Não é necessária autenticação do Client no Authorization-Server, ou seja, no autorization server não é necessário definir o Client-Secret.
- Não funciona com `refresh_token`, não se deve definir o **Client** com `refresh_token`
- O client no **Authorization-server** deve ser definido pelo menos com um `redirect_uri`
- Funciona seguindo o fluxo que se segue:

1. **Resource-Owner** (user) conecta-se à applicação **Client**
2. O **Client** Solicita ao **Authorization-Server** autenticação (redirect)
    ```
    http://AUTHENTICATION-SERVER/oauth/authirize?response_type=token&client_id=CLIENT_ID&state=ABC&redirect_uri=http://CLIENT
   ```
   - o valor do `response_type` é `token`
   - o valor do `redirect_uri` tem de estar registado no **Auhorozation-Server**
   - o valor do `client_id` tem de estar configurado no **Auhorozation-Server**
3. O **Auhorozation-Server** reencaminha/redirecciona o **Resource-Owner** para a pagina de login/autorização
4. Após a autorização do Resource-Owner, o Authorization Server retorna para o Client o access_token atraves de redirect
    ```
   http://CLIENT/#access_token=bla-bla&token_type=bearer&state=ABC&expires_in=1234&scope=read%20write
   ```
5. O Client com access_token mostra a informação normalmente.

##### Para Testar o exemplo:

1. no brower: http://authorization.server.local:8081/oauth/authirize?response_type=token&client_id=webadmin&state=ABC&redirect_uri=http://example-client-app
2. autenticar com john/pwd

---

# [PKCE](https://tools.ietf.org/html/rfc7636)

- O Oauth2 foi construido de forma a que possa ser extendido.
- O PKCE é uma extenção do OAuth2 que tem como objectivo prover maior segurança.
- É recomenda a utilização de **PKCE** quando a aplicação cliente é um **Client** public. Quando a alicação client não consegue garantir a segurança das credenciais do cliente. Por exemplo uma aplicação Client Javascript.
- Garante um nivel de seguraça mais elevado.

```
Code Verifier: very_long_text
Code Challenge: [Plain = same-value-of-Code-Verifier]

code_challenge_method=s256|plain


For Plain:

Code Verifier: very_long_text
Code Challenge: very_long_text

http://authorization.server.local:8081/oauth/authorize?response_type=code&client_id=food4uAnalyticsClient&state=ABC&redirect_uri=http//food4u.local&code_challenge=very_long_text&code_challenge_method=plain
```
---

# Qual o flux a utilizar?

Para tomar esta decisão é preciso responder a algumas perguntas:

- Qual o tipo do client?
    - Public 
        - Quando o Utilizador pode ter acesso ao source-code e ver/alterar o client-secret usado para autenticar o client.
        - Single page application permitem que o ponto anterior aconteça, pelo que são um exemplo de client public.
        - As aplicações nativas, mobile ou desktop tambem são client-public. pois embora sejam compiladas, o codigo é possivel de descompilar.
        
    - Confidencial
        - Quando o código é completamente inacessivel ao utilizador.
            - Uma aplicação web, que corre no server side, não deve expor qualquer informação confidencial ao utilizador.
        - Se a comunicação com o Authorization-Server é feita no servidor e não no browser do utilizador, então Utilizador não terá acesso a qualquer detalhe do processo de autenticação.
        

- O client é Confiavel ou Não Confiável?
    - Confiavel
        - Aplicação própria, que nós proprios ou a nossa empresa implementou
    - Não Confiável
        - Aplicação terceira, usa o Authorization-Server, mas, que não podemos confir nela com 100% de fiabilidade.
        - O objectivos dessa aplicação são desconhecidos para o Authorization-Server. Não sabemos o que essa aplicação faz com os dados.

A decisão do flow a utilizar pode seguir o seguinte algoritmo de de decisão:

![Qual o esquema a utilizar?](diagramas/Oauth2-authentication-flow-decision.png)