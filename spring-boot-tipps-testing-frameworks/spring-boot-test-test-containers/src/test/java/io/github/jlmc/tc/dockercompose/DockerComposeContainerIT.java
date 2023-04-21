package io.github.jlmc.tc.dockercompose;


import io.github.jlmc.tc.domain.todos.services.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.Optional;

@Testcontainers
@SpringBootTest
public class DockerComposeContainerIT {

    private static final String POSTGRES_SERVICE_NAME = "postgres";
    private static final String ABSOLUTE_PATH = "/Users/joao.morgado/Documents/junks/projects/spring/spring-boot-test-libraries/spring-boot-test-test-containers/docker-compose.yml";

    @Container
    private static final DockerComposeContainer<?> dockerCompose = new DockerComposeContainer<>(
            //new File("src/test/resources/test-compose.yml"))
            //new File(ABSOLUTE_PATH))
            new File("../docker-compose.yml"))
            .withExposedService(POSTGRES_SERVICE_NAME, 5432)
            //.withExposedService("api-gw", 3000)
            ;


    /*
    @Container
    private static final PostgreSQLContainer postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.2-alpine");
    */
    @Autowired
    private TodoService subject;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {

        Optional<ContainerState> containerByServiceName = dockerCompose.getContainerByServiceName(POSTGRES_SERVICE_NAME);

        //containerByServiceName.map(t -> t.)

        System.out.println("hello");

        // registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        // registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        // registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    public void contextLoads() {

        System.out.println("...");

    }


}
