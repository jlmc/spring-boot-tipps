package io.github.jlmc.acidrx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @see <a href="https://hantsy.github.io/spring-reactive-sample/data/data-r2dbc.html">spring-reactive-sample</a>
 * @see <a href="https://docs.spring.io/spring-data/r2dbc/docs/current-SNAPSHOT/reference/html/#r2dbc.repositories">spring-reactive-sample</a>
 */
@SpringBootApplication
@EnableR2dbcRepositories
public class AcidRxApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcidRxApplication.class, args);
    }

}
