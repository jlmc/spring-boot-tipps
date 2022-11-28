# 21. Logback

### Localmente:
 - Logback [http://logback.qos.ch](http://logback.qos.ch/)
 
 Em uma application spring-boot o `SL4J` é uma dependencia por defeito, pelo que pode ser utilizado desde logo, sem que seja adicionar qualquer dependencia extra.
 O Spring-Boot possui já uma configuração padrão do Logback, que permite que ele funcione simplemente, sem que se faça qualquer configuração.
 
 
### Cloud:
 - Loggly [https://www.loggly.com/](https://www.loggly.com/)
 
 Quando se faz deploy de uma aplicação na cloud, não é boa idea guardar os logger localmente na maquina na qual esta a correr a applicação. 
   - Na cloud usam-se normalmente conteiners, os quais são descartaveis. Com tal os logger desaparecem quando se descarta o conteiner.
   - Imaginando que se têm varias instancias de uma aplication (varios containers). Se cada uma dessas instancias estiver a loggar localmente podemos ter de procurar um eventual problema em muitos ficheiros de logger.

 Para resolver os problemas anteriores usa-se uma solução centralizada. O [Loggly](https://www.loggly.com/) é uma delas.
 
 
##### Configuração:

1. Adicionar o ficheiro de appender, por defeito o spring-boot usa o ficheiro com o nome: logback-spring.xml na pasta resource. A documentação pode ser encontrada em [https://www.loggly.com/docs/java-logback/](https://www.loggly.com/docs/java-logback/)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    
    <!--
        Incluir/importar a configurações default do spring boot (consola) caso não se inclua isto, nada será logado na consola por defeito.
    -->
    <include resource="org/springframework/boot/logging/logback/base.xml" />
    
    <!-- 
        Para obter uma application property: logging.loggly.token
        Será usada para configurar o valor do TOKEN na definição do loggly
    -->
    <springProperty name="logglyToken" source="logging.loggly.token" />
    
    <!--
        Configuração do loggly, exemplo da documentation
    -->
    <appender name="loggly" class="ch.qos.logback.ext.loggly.LogglyAppender">
        <endpointUrl>https://logs-01.loggly.com/inputs/${logglyToken}/tag/logback</endpointUrl>
        <pattern>%d{"ISO8601", UTC} %p %t %c %M - %m%n</pattern>
    </appender>
    
    <!-- Torna o registo do logger asyncrono, é um wrapper à volta do  loggly -->
    <appender name="logglyAsync" class="ch.qos.logback.classic.AsyncAppender">
        <appender-ref ref="loggly" />
    </appender>
    
    <root level="info">
        <appender-ref ref="logglyAsync" />
    </root>
    
</configuration>
```


2. pom.xml
```xml
<project>

    <properties>
     <logback-ext-loggly.version>0.1.5</logback-ext-loggly.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.logback-extensions</groupId>
            <artifactId>logback-ext-loggly</artifactId>
            <version>${logback-ext-loggly.version}</version>
        </dependency>
    </dependencies>

</project>
```
 
 