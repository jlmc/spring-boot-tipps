# Getting Started

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.4/maven-plugin/reference/html/#build-image)
* [Testcontainers MongoDB Module Reference Guide](https://www.testcontainers.org/modules/databases/mongodb/)
* [Spring Data MongoDB](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#data.nosql.mongodb)
* [Validation](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#io.validation)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#web.reactive)
* [Testcontainers](https://www.testcontainers.org/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.0.4/reference/htmlsingle/#using.devtools)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

## configure mongo connection

```
mongodb://<username>:<password>@<host>:<port>/<database>
```

```properties
spring.data.mongodb.uri="mongodb://admin:password@localhost:27017/database-name"
```
or alternatively

```properties
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=database-name
spring.data.mongodb.username=admin
spring.data.mongodb.password=password
```


## running

```shell
mvn spring-boot:run \
 -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```


```
mvn spring-boot:run -Dspring-boot.run.arguments='--spring.data.mongodb.port=7017 --spring.data.mongodb.host=localhost'
```


## spring data most common annotations

https://www.baeldung.com/spring-data-annotations


## Open CSV

https://opencsv.sourceforge.net/#multivaluedmap_based_bean_fields_many_to_one_mappings

## Open api
http://localhost:8080/webjars/swagger-ui/index.html


http://localhost:8080/v3/api-docs.yaml
