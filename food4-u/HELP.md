# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/maven-plugin/)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/htmlsingle/#using-boot-devtools)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)


### Project infrastructure

- [mysql](https://hub.docker.com/_/mysql?tab=tags)

```
docker pull mysql:8.0.18
```


```
docker run --name demos-mysql \
 -v $(pwd)/mysql/own/datadir:/var/lib/mysql \
 -e MYSQL_ROOT_PASSWORD=my-secret-pw \
 -e MYSQL_DATABASE=food4u \
 -e MYSQL_USER=food4u \
 -e MYSQL_PASSWORD=food4u \
 -p 3306:3306 \
 -d mysql:8.0.18
```



```
curl -X GET -v \
    -H "Accept:application/xml" \
    -H "Content-Type:application/xml" \
    http://localhost:8080/cookers 
```
