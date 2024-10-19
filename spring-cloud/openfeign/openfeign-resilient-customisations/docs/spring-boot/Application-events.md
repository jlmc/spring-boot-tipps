In a **Spring Boot** application, events are an important part of the application lifecycle and provide a way to listen for specific application states or actions. The lifecycle of a Spring `SpringApplicationEvent` is tied to the phases that a Spring Boot application goes through during its startup, running, and shutdown.

### 1. **Overview of Spring Application Events**
Spring provides several predefined events related to the lifecycle of the application, from its start to its shutdown. These events are published at various stages, and custom events can also be created if needed.

The typical lifecycle events include:

- `ApplicationStartingEvent`
- `ApplicationEnvironmentPreparedEvent`
- `ApplicationContextInitializedEvent`
- `ApplicationPreparedEvent`
- `ApplicationStartedEvent`
- `ApplicationReadyEvent`
- `ApplicationFailedEvent`

### 2. **Lifecycle Phases and Events**
The main phases and the corresponding `SpringApplicationEvent` classes in Spring Boot are as follows:

#### a. **ApplicationStartingEvent**
- **When**: Published as soon as the application starts and before any processing is done.
- **Purpose**: This event signals that the application is starting. At this point, the logging system is not fully initialized, so logging might be limited.
- **Use case**: Often used to set up early configuration, like changing environment settings or custom initialization before the context is created.

#### b. **ApplicationEnvironmentPreparedEvent**
- **When**: Published when the `Environment` (e.g., system properties, profiles, environment variables) is ready, but before the `ApplicationContext` is created.
- **Purpose**: The environment is prepared, but the context is not yet available. You can use this to configure properties or profiles before they are used by beans.
- **Use case**: Customize properties sources or profiles before the beans in the context start being created.

#### c. **ApplicationContextInitializedEvent**
- **When**: Published when the `ApplicationContext` has been initialized but before any beans are loaded.
- **Purpose**: This event is available in Spring Boot 2.4 and later and signals that the context is initialized, but the bean definitions are not loaded yet.
- **Use case**: Used to modify or configure the `ApplicationContext` itself before it starts loading beans.

#### d. **ApplicationPreparedEvent**
- **When**: Published when the `ApplicationContext` is fully loaded but not yet refreshed (i.e., before any bean is created).
- **Purpose**: This event allows for last-minute changes to the context before the application starts.
- **Use case**: Ideal for tasks like modifying bean definitions or adding custom beans before the context is refreshed.

#### e. **ApplicationStartedEvent**
- **When**: Published when the `ApplicationContext` is refreshed and the application is fully started but before the application runs any `CommandLineRunner` or `ApplicationRunner` beans.
- **Purpose**: This event signifies that the application is ready to begin executing its code but has not yet begun any execution.
- **Use case**: Use this event if you want to perform tasks after the context has been fully initialized but before running logic like `CommandLineRunner`.

#### f. **ApplicationReadyEvent**
- **When**: Published when the application is ready to service requests.
- **Purpose**: Signals that the application is fully started and ready for requests. All initialization is complete at this point.
- **Use case**: This is the final event in the startup sequence. It is ideal for tasks such as sending startup notifications or setting up any final resources.

#### g. **ApplicationFailedEvent**
- **When**: Published if the application fails to start due to an exception.
- **Purpose**: Signals that the application encountered an error during startup.
- **Use case**: Used to log or react to startup errors, perform cleanup, or notify external systems.

### 3. **How to Listen to These Events**
You can listen to these events by implementing the `ApplicationListener` interface or by using the `@EventListener` annotation. For example:

```java
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener {

    @EventListener
    public void onApplicationReady(ApplicationReadyEvent event) {
        System.out.println("Application is ready to serve requests!");
    }
}
```

Or, using `ApplicationListener`:

```java
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CustomStartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("Application has started!");
    }
}
```

### 4. **Custom Events**
You can also define custom events in your application:

```java
import org.springframework.context.ApplicationEvent;

public class CustomEvent extends ApplicationEvent {
    public CustomEvent(Object source) {
        super(source);
    }
}
```

And then publish the event using the `ApplicationEventPublisher`:

```java
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CustomEventPublisher {

    private final ApplicationEventPublisher publisher;

    public CustomEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish() {
        publisher.publishEvent(new CustomEvent(this));
    }
}
```

To listen for the custom event:

```java
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomEventListener {

    @EventListener
    public void handleCustomEvent(CustomEvent event) {
        System.out.println("Custom event triggered!");
    }
}
```

### 5. **Shutdown Event: ContextClosedEvent**
While the previous events relate to application startup, Spring also provides a `ContextClosedEvent` to handle tasks during the shutdown phase:

- **When**: Published when the `ApplicationContext` is closed, such as during application shutdown.
- **Use case**: Useful for cleanup tasks like closing connections, releasing resources, etc.

---

### **Summary of SpringApplicationEvent Lifecycle**

1. **ApplicationStartingEvent**: Application is starting, no context or environment yet.
2. **ApplicationEnvironmentPreparedEvent**: Environment is prepared, context not initialized.
3. **ApplicationContextInitializedEvent**: Context initialized, no beans loaded yet.
4. **ApplicationPreparedEvent**: Context loaded but not refreshed (beans not created yet).
5. **ApplicationStartedEvent**: Context refreshed, application is starting.
6. **ApplicationReadyEvent**: Application is ready to handle requests.
7. **ApplicationFailedEvent**: Application failed during startup.

By leveraging these events, you can hook into various stages of a Spring Boot application's lifecycle and perform specific actions or configurations accordingly.


---

In **Spring Boot**, both `CommandLineRunner` and `ApplicationRunner` interfaces are used to run specific code after the application has started. They allow you to execute some logic at startup, typically after the `SpringApplication` has finished the bootstrap process and the Spring context has been initialized. However, there are subtle differences between the two, particularly in how they handle command-line arguments.

### 1. **CommandLineRunner**

- **Purpose**: The `CommandLineRunner` interface is used to execute logic immediately after the Spring Boot application has started. It provides a simple way to access the raw command-line arguments passed to the application.

- **Signature**:
  ```java
  @FunctionalInterface
  public interface CommandLineRunner {
      void run(String... args) throws Exception;
  }
  ```

- **Arguments**: The `run` method in `CommandLineRunner` accepts a `String... args` parameter, which directly passes the raw command-line arguments in an array of strings.

- **Use case**: It's best for cases where you need to access the raw, unparsed command-line arguments.

- **Example**:
  ```java
  import org.springframework.boot.CommandLineRunner;
  import org.springframework.stereotype.Component;

  @Component
  public class MyCommandLineRunner implements CommandLineRunner {

      @Override
      public void run(String... args) throws Exception {
          System.out.println("Executing CommandLineRunner");
          for (String arg : args) {
              System.out.println("Argument: " + arg);
          }
      }
  }
  ```

### 2. **ApplicationRunner**

- **Purpose**: The `ApplicationRunner` interface is very similar to `CommandLineRunner`, but instead of passing raw command-line arguments, it provides an `ApplicationArguments` object that gives more flexibility for parsing arguments.

- **Signature**:
  ```java
  @FunctionalInterface
  public interface ApplicationRunner {
      void run(ApplicationArguments args) throws Exception;
  }
  ```

- **Arguments**: The `run` method in `ApplicationRunner` accepts an `ApplicationArguments` object, which provides methods to retrieve arguments in a more structured way (e.g., option arguments and non-option arguments).

    - `args.getSourceArgs()`: Returns all arguments as a raw string array.
    - `args.getOptionNames()`: Returns a set of option argument names (e.g., `--name=value`).
    - `args.getOptionValues("name")`: Retrieves the values associated with an option argument.
    - `args.getNonOptionArgs()`: Retrieves non-option arguments.

- **Use case**: Use this when you want more control over parsing and managing command-line arguments, especially when dealing with options.

- **Example**:
  ```java
  import org.springframework.boot.ApplicationArguments;
  import org.springframework.boot.ApplicationRunner;
  import org.springframework.stereotype.Component;

  @Component
  public class MyApplicationRunner implements ApplicationRunner {

      @Override
      public void run(ApplicationArguments args) throws Exception {
          System.out.println("Executing ApplicationRunner");
          
          // Get all arguments
          String[] sourceArgs = args.getSourceArgs();
          System.out.println("Source Arguments: " + String.join(", ", sourceArgs));

          // Check if specific options exist
          if (args.containsOption("name")) {
              System.out.println("Name option: " + args.getOptionValues("name"));
          }

          // Get non-option arguments
          for (String nonOptionArg : args.getNonOptionArgs()) {
              System.out.println("Non-option argument: " + nonOptionArg);
          }
      }
  }
  ```

### 3. **Key Differences**

| Feature                 | **CommandLineRunner**                              | **ApplicationRunner**                                |
|-------------------------|----------------------------------------------------|------------------------------------------------------|
| **Method signature**    | `run(String... args)`                              | `run(ApplicationArguments args)`                     |
| **Command-line access** | Accesses raw `String` arguments                    | Accesses `ApplicationArguments` (parsed arguments)   |
| **Argument parsing**    | No built-in parsing; you manually handle arguments | Provides methods to handle options and non-options   |
| **Use case**            | Best for simple argument handling or no arguments  | Best for structured parsing of options and arguments |

### 4. **Common Behavior**
- Both `CommandLineRunner` and `ApplicationRunner` execute after the Spring application context is initialized but before the application is considered fully ready (just before the `ApplicationReadyEvent` is fired).
- You can have multiple `CommandLineRunner` and `ApplicationRunner` beans in your application. If multiple runners exist, their execution order can be controlled using the `@Order` annotation or by implementing the `Ordered` interface.

### 5. **Execution Order Example (using @Order)**

You can control the order in which multiple `CommandLineRunner` or `ApplicationRunner` beans are executed using the `@Order` annotation:

```java
import org.springframework.core.annotation.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FirstRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("First CommandLineRunner");
    }
}

@Component
@Order(2)
public class SecondRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("Second CommandLineRunner");
    }
}
```

This will ensure that `FirstRunner` runs before `SecondRunner`.

### 6. **When to Use Each**

- **Use `CommandLineRunner`**:
    - When you only need to access the raw, unparsed command-line arguments.
    - For simpler cases where argument parsing or structure is not important.

- **Use `ApplicationRunner`**:
    - When you need a structured way of parsing command-line arguments.
    - If you want to differentiate between option arguments (like `--key=value`) and non-option arguments.

### 7. **Conclusion**
Both `CommandLineRunner` and `ApplicationRunner` provide a way to execute code at startup, but the choice between the two comes down to how you want to handle command-line arguments:
- `CommandLineRunner` is simpler and gives raw access to the arguments.
- `ApplicationRunner` is more flexible and powerful, especially when parsing structured options.

In most cases, if you are dealing with complex command-line options, `ApplicationRunner` is preferred. For simple use cases, `CommandLineRunner` is sufficient.