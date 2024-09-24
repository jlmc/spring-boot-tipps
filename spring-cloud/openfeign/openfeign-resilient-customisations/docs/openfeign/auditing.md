# Audit requests and responses

## To audit or log requests and responses in Feign, you can configure a logger to capture detailed information about HTTP requests and responses. Feign supports different logging levels, which can be configured to capture varying levels of detail.

Here's how you can set up Feign to audit (log) requests and responses:

### 1. **Add Dependencies for Logging**

If you're using SLF4J (a popular logging framework in Java), you need to include the SLF4J and SLF4J bindings dependencies in your project. If you're using Spring Boot, these may already be included.

For Maven:

```xml
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-slf4j</artifactId>
    <version>12.5</version> <!-- Make sure to use the latest version -->
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.32</version> <!-- Make sure to use the compatible version -->
</dependency>

<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.32</version> <!-- Make sure to use the compatible version -->
</dependency>
```

For Gradle:

```groovy
implementation 'io.github.openfeign:feign-slf4j:12.5' // Use the latest version
implementation 'org.slf4j:slf4j-api:1.7.32' // Use the compatible version
implementation 'org.slf4j:slf4j-simple:1.7.32' // Use the compatible version
```

### 2. **Configure Feign Logger**

You can use the `Slf4jLogger` provided by Feign to log requests and responses. Here's how to configure it:

```java
import feign.Feign;
import feign.Logger;
import feign.slf4j.Slf4jLogger;

public class Main {
    public static void main(String[] args) {
        UserClient userClient = Feign.builder()
            .logger(new Slf4jLogger(UserClient.class)) // Set up SLF4J Logger
            .logLevel(Logger.Level.FULL) // Set log level to FULL for auditing requests and responses
            .target(UserClient.class, "https://api.example.com");

        UserClient.User user = userClient.getUserById(1L);
        System.out.println("User ID: " + user.getId());
        System.out.println("User Name: " + user.getName());
    }
}
```

### 3. **Logging Levels in Feign**

Feign provides several logging levels that determine the granularity of the logging output:

- `NONE` – No logging (default).
- `BASIC` – Logs only the request method and URL, response status code, and execution time.
- `HEADERS` – Logs the basic information along with request and response headers.
- `FULL` – Logs the request and response headers, body, and metadata.

To achieve full auditing of requests and responses, use `Logger.Level.FULL`.

### 4. **Example of Full Logging Output**

When `Logger.Level.FULL` is enabled, you'll see output similar to this:

```
[UserClient#getUserById] ---> GET https://api.example.com/users/1 HTTP/1.1
[UserClient#getUserById] REQUEST HEADERS:
[UserClient#getUserById] Content-Type: application/json
[UserClient#getUserById] REQUEST BODY:
[UserClient#getUserById] { "id": 1 }
[UserClient#getUserById] ---> END HTTP (14-byte body)
[UserClient#getUserById] <--- HTTP/1.1 200 OK (210ms)
[UserClient#getUserById] RESPONSE HEADERS:
[UserClient#getUserById] Content-Type: application/json
[UserClient#getUserById] RESPONSE BODY:
[UserClient#getUserById] { "id": 1, "name": "John Doe" }
[UserClient#getUserById] <--- END HTTP (29-byte body)
```

This logs the entire request and response cycle, including headers and bodies, making it ideal for debugging and auditing purposes.

### 5. **Customize Logging Configuration (Optional)**

You can customize how logs are formatted and managed using your SLF4J configuration. For instance, if you're using Logback, you can adjust your `logback.xml` or `logback-spring.xml` configuration to customize logging levels, patterns, and outputs.

### Summary

By configuring Feign with `Slf4jLogger` and setting the log level to `FULL`, you can easily audit all HTTP requests and responses. This is useful for debugging, monitoring, and ensuring the correctness of interactions with external services.


--

## To audit Feign requests and responses and store them in a database, you can create a custom `Feign` interceptor or a `Logger` implementation. This custom component will capture HTTP requests and responses and then store the details in a database using an ORM like Hibernate, JPA, or Spring Data JPA.

### Steps to Audit Feign Requests and Responses to a Database

1. **Create an Entity to Store Request and Response Logs**
2. **Create a Repository Interface to Save Logs**
3. **Create a Custom Feign Logger or Interceptor**
4. **Configure Feign to Use the Custom Logger or Interceptor**

### 1. Create an Entity to Store Request and Response Logs

First, define a JPA entity class to represent the request and response logs in the database.

```java
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FeignRequestResponseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String httpMethod;
    
    @Lob // For larger texts
    private String requestBody;
    
    @Lob
    private String responseBody;
    
    private int responseStatus;
    private LocalDateTime timestamp;

    // Constructors, Getters, and Setters

    public FeignRequestResponseLog() {
    }

    public FeignRequestResponseLog(String url, String httpMethod, String requestBody, String responseBody, int responseStatus) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.responseStatus = responseStatus;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
}
```

### 2. Create a Repository Interface to Save Logs

Create a Spring Data JPA repository interface to handle database operations for the `FeignRequestResponseLog` entity.

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeignRequestResponseLogRepository extends JpaRepository<FeignRequestResponseLog, Long> {
}
```

### 3. Create a Custom Feign Logger

To capture the request and response data, you can create a custom Feign `Logger` that extends `feign.Logger` and overrides its methods to log request and response details.

```java
import feign.Logger;
import feign.Request;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFeignLogger extends Logger {

    @Autowired
    private FeignRequestResponseLogRepository logRepository;

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        String url = request.url();
        String httpMethod = request.httpMethod().name();
        String requestBody = request.requestBody().asString();

        // Save request information in the database
        FeignRequestResponseLog log = new FeignRequestResponseLog();
        log.setUrl(url);
        log.setHttpMethod(httpMethod);
        log.setRequestBody(requestBody);

        logRepository.save(log);  // Save initial request info
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        String responseBody = response.body() != null ? response.body().toString() : null;
        int status = response.status();

        // Retrieve the last log entry for the request (by some criteria or ID)
        FeignRequestResponseLog log = logRepository.findTopByOrderByTimestampDesc();
        if (log != null) {
            log.setResponseBody(responseBody);
            log.setResponseStatus(status);
            logRepository.save(log);  // Update log with response info
        }

        return response;
    }
}
```

### 4. Configure Feign to Use the Custom Logger

You need to configure Feign to use this custom logger. If you're using Spring Boot, this can be done by defining a Feign client configuration.

```java
import feign.Feign;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Autowired
    private CustomFeignLogger customFeignLogger;

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .logger(customFeignLogger)  // Use custom logger
                .logLevel(Logger.Level.FULL);  // Set log level to FULL
    }
}
```

### 5. Use the Feign Client with Custom Logger

Now, when you create your Feign client, it will use the `CustomFeignLogger` and save all request and response data into the database.

```java
import feign.Feign;

public class Main {
    public static void main(String[] args) {
        UserClient userClient = Feign.builder()
            .target(UserClient.class, "https://api.example.com");

        UserClient.User user = userClient.getUserById(1L);
        System.out.println("User ID: " + user.getId());
        System.out.println("User Name: " + user.getName());
    }
}
```

### Summary

- **Entity:** Define a JPA entity for logs.
- **Repository:** Create a Spring Data JPA repository to manage logs.
- **Custom Logger:** Implement a custom `Logger` to capture requests and responses.
- **Configuration:** Configure Feign to use this custom logger.

With this setup, every HTTP request and response through Feign will be audited and stored in your database, providing a full audit trail for your application's external API interactions.