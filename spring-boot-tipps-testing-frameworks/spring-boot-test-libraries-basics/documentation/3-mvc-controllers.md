# MVC - controllers 

Para implementar integration Tests em MVC é necessarios conhecer um pouco melhor a anotação **`@SpringBootTest`**

A anotação **`@SpringBootTest`** vai adicionar algumas funcionalidade ao contexto de testes do spring, mas ela não inicia de forma automatica um servidor web.

O modulo web do spring usa o tomcat por padrão. para inicia-lo no tests temos de preencher o atributo **WebEnviroment**.

O **WebEnviroment** é um enum, ele vai auxiliar na configuraçãp de como um servidor web será iniciado, ele tem as seguintes opções:

* **MOCK** - Cria um WebApplicationContext com um ambiente de servlet simulado se as APIs de servlet estiverem no classpath, um ReactiveWebApplicationContext se o Spring WebFlux estiver no classpath ou ApplicationContext.

* **RANDOM_PORT** - Cria um contexto de aplicativo da web (reativo ou baseado em servlet) e define a propriedade de ambente **servet.port = 0** (que geralmente aciona a escuta em uma porta aleatória).

* **DEFINED_PORT** - cria um contexto de aplicativo da web (reactivo) sem definir qualquer propriedade de ambiente **server.port = 0**.

* **NONE** - cria um ApplicationContext e define que não deve ser executado como um aplicativo web e não deve iniciar um servido da web.


<h5>O primeiro exemplo que temos para analisar é o seguinte</h5>

```java
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerIT {

    @Test
    public void testing() {
        Assert.assertTrue(true);
    }

}
```

Este exemplo faz uso da configuração @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
que irá tambem levantar um servidor tomcat para executar os testes. 
Por levantar o servidor web isso tem custo de tempo adicional.

para contornar a limitação do exemplo anterior podemos usar a anotação @AutoConfigureMockMvc:


```java
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerMockMvcIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldShowAllTasks() throws Exception {
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/views/tasks"));
    }

}
```

**MockMvc** - é uma classe que vai simular o processamento das Http Requisitions, vai executar um Requisition e vai retornar a resposta dessa Requisition sem levantar qualquer servidor web.

Para configurar o MockMvc é necessario utilizar a anotação @AutoConfigureMockMvc.

**@AutoConfigureMockMvc** - vai fazer toda a parte de configuração básica do mockMvc.
O mockMvc possui apenas um metodo *perform* 


### RequestBuilder, perform e ResultActions

RequestBuilder - prepara o request para ser executado;
Perform - executa a requisição;
ResultActions -  permite que façamos checks sobre o resultado do request.


O nome da classe MockMvcRequestBuilders é um pouco enganador, por faz nos pensar que é um Builder, mas a verdade é que é uma factory.

a chamada d method get(String) vai retornar uma instancia de MockHttpServletRequestBuilder que esse sim, é um request builder.


o que aconteceu:

1. foi feito um requisição para o endereço mapeado como "/views/tasks"
2. a mockMvc processou essa requisição, e com isso o method do controller foi invocado
3. por fim ainda foi verificado que o resultado foi um 200.
4. sem qualquer servidor web.


## Metodos do ResultActions

* andDo: 
    Realiza alguma actividade definida por um objecto ResultHandler.
    MockMvcResultHandlers, 
    A classe usada para criar ResultHandlers. Possui os metodos:
        * Log - que escreve os detalhes do MvcResult como uma mensagem de log via Apache Commons Logging usando a categoria de log org.springframework.test.web.servlet.result.
        * print() - escreve os detalhes do MvcResult no output stream padrão.
        * print(OutputStream stream) - escreve os detalhes do MvcResult no object OutputStream passado como parametro.
        * print(Writer writer) - escreve os detalhes do MvcResult no object Writer passado como parâmetro.


* and Return:
