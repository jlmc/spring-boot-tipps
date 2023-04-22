package io.github.jlmc.tc;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import io.github.jlmc.tc.domain.todos.repositories.TodoRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TodoIntegrationMultipleContainersTests {

    /**
     * static - one container per class test
     */
    @Container
    private static final PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.2-alpine")
            //.withPassword("pwd")
            //.withUsername("user")
            //.withDatabaseName("demo")
            ;
    @Container
    private static final RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3-management")
              .withFileSystemBind("/Users/joao.morgado/Documents/junks/projects/spring/test-containers-postgres/docker/rabbitmq/rabbitmq.config", "/etc/rabbitmq/rabbitmq.config", BindMode.READ_ONLY)
              .withFileSystemBind("/Users/joao.morgado/Documents/junks/projects/spring/test-containers-postgres/docker/rabbitmq/rabbitmq-definitions.json", "/etc/rabbitmq/definitions.json", BindMode.READ_ONLY);

    @Autowired
    private TodoRepository repository;

    /*
    @Container
    private static GenericContainer genericContainer =
            new GenericContainer("my-image:latest");
     */

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void when_using_a_clean_db_this_should_be_empty() {
        List<Todo> all = repository.findAll();
        Assertions.assertEquals(1, all.size());
    }


    @Test
    @Ignore
    void what_we_can_do() throws Exception {

        // put files into container
        rabbitMQContainer
                .withClasspathResourceMapping(
                        "application.properties",
                        "/temp/application.properties",
                        BindMode.READ_ONLY);

        rabbitMQContainer.withFileSystemBind("/Users/joao.morgado/Documents/junks/projects/spring/test-containers-postgres/docker/rabbitmq/rabbitmq.config", "/etc/rabbitmq/rabbitmq.config", BindMode.READ_ONLY);
        rabbitMQContainer.withFileSystemBind("/Users/joao.morgado/Documents/junks/projects/spring/test-containers-postgres/docker/rabbitmq/rabbitmq-definitions.json", "/etc/rabbitmq/definitions.json", BindMode.READ_ONLY);

        // execute a comand
        rabbitMQContainer.execInContainer("ls", "-la");

        // get Container logs
        String stdout = rabbitMQContainer.getLogs(OutputFrame.OutputType.STDOUT);

        // with consumer logger
        rabbitMQContainer.withLogConsumer(of -> System.out.println(new String(of.getBytes())));


        // get Port mapping
        //       - "5672:5672"
        //      - "15672:15672"
        Integer mappedPortOnYourMachine = rabbitMQContainer.getMappedPort(5672);
        Integer mappedPort2OnYourMachine = rabbitMQContainer.getMappedPort(15672);
    }
}
