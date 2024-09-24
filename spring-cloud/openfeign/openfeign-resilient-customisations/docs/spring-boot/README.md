# Spring Boot

- Content
  - [Start a Spring Boot application in debug mode using Maven](#start-a-spring-boot-application-in-debug-mode-using-maven)

## start a spring boot application in debug mode using maven 
To start a Spring Boot application in debug mode using Maven, you need to use the `mvn` command with specific arguments to enable debugging. This is useful when you want to attach a debugger (like IntelliJ IDEA, Eclipse, or Visual Studio Code) to the running application.

### Steps to Start a Spring Boot Application in Debug Mode with Maven

1. **Open your terminal or command prompt.**

2. **Navigate to the directory of your Spring Boot application** where the `pom.xml` file is located.

3. **Run the following Maven command to start the application in debug mode:**

   ```bash
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
   ```

    - `-Dspring-boot.run.jvmArguments` sets JVM arguments for the `spring-boot:run` goal.
    - `-Xdebug` enables the debugging mode.
    - `-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005` specifies the debugging settings:
        - `transport=dt_socket`: Uses a socket-based transport for debugging.
        - `server=y`: Enables the debug server.
        - `suspend=n`: Starts the application without waiting for the debugger to attach.
        - `address=5005`: Sets the port for the debugger to attach to (you can change this port if needed).

4. **Attach your debugger** to the application using port `5005`.

### Alternative: Debug Directly from an IDE

If you are using an IDE like IntelliJ IDEA or Eclipse, you can run the Spring Boot application in debug mode directly from the IDE:

- **For IntelliJ IDEA**: Right-click on the main class (annotated with `@SpringBootApplication`) or the Maven run configuration and select **Debug**.
- **For Eclipse**: Right-click on the main class, go to **Run As**, and choose **Debug as Spring Boot App**.

This way, the IDE will handle the debug configurations automatically, and you can add breakpoints and start debugging right away.