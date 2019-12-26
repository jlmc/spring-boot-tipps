package io.costax.food4u;

import io.costax.food4u.domain.repository.internal.CustomJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.time.ZoneId;
import java.util.TimeZone;

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
        //We must be careful when we use the method 'TimeZone.setDefault ()' it affects all applications running on the same JVM
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("UTC").getId()));
        SpringApplication.run(Food4UApplication.class, args);
    }

}
