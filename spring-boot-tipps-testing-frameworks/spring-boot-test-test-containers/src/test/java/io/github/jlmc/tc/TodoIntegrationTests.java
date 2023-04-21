package io.github.jlmc.tc;

import io.github.jlmc.tc.domain.todos.entities.Todo;
import io.github.jlmc.tc.domain.todos.repositories.TodoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TodoIntegrationTests {

    @Autowired
    private TodoRepository repository;

    /**
     * static - one container per class test
     */
    @Container
    private static PostgreSQLContainer container =
            new PostgreSQLContainer<>("postgres:13.2-alpine")
                    //.withPassword("pwd")
                    //.withUsername("user")
                    //.withDatabaseName("demo")
            ;

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
