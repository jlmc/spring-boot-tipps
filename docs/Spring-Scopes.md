# Spring Scope

We know the **Spring Inversion of Control** Container ([IoC Container](https://www.amitph.com/spring-dependency-injection-inversion-control/#Inversion_of_Control_Container_IoC_Container)), creates and manages the beans in a Spring Application. Each bean we define, as a Spring Bean, declares its dependency and ways to provide the [dependency injection](https://www.amitph.com/spring-dependency-injection-inversion-control/). Along with this, the beans can also specify how many instances of the bean should get created and in what scope they should stay alive.

This is done by the `@Scope` in the annotation based configurations or `scope` attribute of `bean` tag on a XML based configurations. Below is the list of Scopes available in Spring.

- Singleton (default)
- Prototype
- Request
- Session
- Application

- Out of these 5, the **Singleton** and **Prototype** scopes are called as Standard Scopes and are available in an ApplicationContext. The other scopes like **Request**, **Session**, and **Application** are available only in the Web Based Applications. We will look at each of them with the help of simple examples.


## Singleton Scope

**Singleton** object is an important concept of **Object Oriented Programming**. An object is called as Singleton if one and only one instance of that object is created. Whenever, any part of the application wants to access the object, exactly the same instance of the object is returned.

Any Spring Bean, by default, is **Singleton**. When two classes auto-wire a class they both get same instance. Alternatively a bean can explicitly declare itself as Singleton, like below.

Annotation based configuration

```java
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class TodoService {}
```

XML configuration

```xml
<bean id="todoService" class="io.github.jlmc.domain.service.TodoService" scope="singleton" />
```

Example of use:

```java
// Bean 1
@Component
public class DogsService {
    @Autowired private TodoService dao;

    public TodoService getDao() {
        return dao;
    }
}

// Bean 2
@Component
public class DogsService2 {
    @Autowired private TodoService dao;

    public TodoService getDao() {
        return dao;
    }
}

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired DogsService service1;

    @Autowired DogsService2 service2;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(service1.getDao().equals(service2.getDao()));
    }
}
```

> When we run the above code the output we get is true. This indicates that both of the services got exactly the same instance of Dao.




## Prototype Scope

The **prototype** scope is exactly opposite to the singleton. That means, when any bean tries to auto-wire a **prototype** bean, every time a new instance is created and assigned. Below is the way, a bean can declare itself as prototype.

Annotation Based Configuration

```java
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class TodoService {}
```

```xml
<bean id="todoService" class="io.github.jlmc.domain.service.TodoService" scope="prototype" />
```


Example of use:

```java
// Bean 1
@Component
public class DogsService {
    @Autowired private TodoService dao;

    public TodoService getDao() {
        return dao;
    }
}

// Bean 2
@Component
public class DogsService2 {
    @Autowired private TodoService dao;

    public TodoService getDao() {
        return dao;
    }
}

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired DogsService service1;

    @Autowired DogsService2 service2;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(service1.getDao().equals(service2.getDao()));
    }
}
```

> The equality check here, results in false, which indicates both of the services got a separate instances of Dao.


## Request Scope

The **request** scope is only available in the Web Applications. An instance of a bean with request scope is created for each HTTP request and remain available till the life of that request.

Below is the way a bean can be declared in request scope.

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DogDto {
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
```

> Why do we need proxyMode = ScopedProxyMode.TARGET_CLASS ?
>> Because, the bean has request scope. That means an instance of the bean won’t be created until there is a request. But the classes auto-wire this bean (like Controller in our case) gets instantiated on the Web Application startup. Spring then creates a Proxy instance and inject in the controller. When the controller receives a request the proxy instance is replaced with actual one


## Session Scope

When a bean declares its scope as **session**, the bean remains alive in the session. For each session a new instance will be created. To try and see this running, let’s reuse the example we saw in last section. The only change is the bean has **session** scope.

```java
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
```

When we run the same GET twice from same browser, in the second request we should see the previous request value is persisted. While, if we make second request from a different browser the dto is new.

## Application Scope

The bean marked with scope **application** is created only once per Web Application. It is created when the Application starts and destroyed when the application stops. The application scope is no different than singleton except that the singleton bean gets created in an ApplicationContext while the bean with application scope is created in a WebApplicationContext.

We can try the application scope, with the same example used above. Only the difference is scope attribute in the annotation.

```java
@Scope(value = WebApplicationContext.SCOPE_APPLICATION, proxyMode = ScopedProxyMode.TARGET_CLASS)
```

