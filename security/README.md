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