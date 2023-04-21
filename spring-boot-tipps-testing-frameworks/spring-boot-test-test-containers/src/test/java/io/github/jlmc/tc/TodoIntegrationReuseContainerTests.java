package io.github.jlmc.tc;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import io.github.jlmc.tc.domain.todos.repositories.TodoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

/**
 *  1. one your home directory create the file .testcontainers.properties with the content: testcontainers.reuse.enable=true
 *
 * use .withReuse(true) for our container definition
 * opt-in for reusable containers inside our ~/.testcontainers.properties file
 * use a manual container lifecycle control: singleton containers or respectively start the containers manually with e.g. @BeforeAll (it won't work with the JUnit 4 rule or JUnit Jupiter extension @Testcontainers)
 *
 */


//@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TodoIntegrationReuseContainerTests {

    @Autowired
    private TodoRepository repository;

    /**
     * static - one container per class test
     */
    //@Container
    private static PostgreSQLContainer container =
            new PostgreSQLContainer<>("postgres:13.2-alpine")
                    .withReuse(true)
                    //.withPassword("pwd")
                    //.withUsername("user")
                    //.withDatabaseName("demo")
            ;

    @BeforeAll
    public static void setup() {
        container.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> container.getJdbcUrl());
        registry.add("spring.datasource.username", () -> container.getUsername());
        registry.add("spring.datasource.password", () -> container.getPassword());
    }

    @Test
    void when_using_a_clean_db_this_should_be_empty() {
        List<Todo> all = repository.findAll();
        Assertions.assertEquals(1, all.size());
    }
}
