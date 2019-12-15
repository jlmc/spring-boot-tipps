package io.costax.food4u;

import io.costax.food4u.domain.repository.internal.CustomJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * By default we don't need to use the @EnableJpaRepositories annotation,
 * because the JpaRepositories are already enable by default.
 * This annotation is used when we need to customise any Spring Data JPA repository,
 * like the repositoryImplementationPostfix='Impl' for example.
 * In this applications we are extends the default JPA Repository
 */
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl.class)
public class Food4UApplication {

    public static void main(String[] args) {
        SpringApplication.run(Food4UApplication.class, args);
    }

}
