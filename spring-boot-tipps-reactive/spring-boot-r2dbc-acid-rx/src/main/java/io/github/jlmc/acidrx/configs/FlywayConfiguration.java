package io.github.jlmc.acidrx.configs;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h3>Configuration for Spring R2DBC and Flyway.</h3>
 *
 * Since Flyway does not work with R2DBC,
 * we'll need to create the Flyway bean with the init method migrate(),
 * which prompts Spring to run our migrations as soon as it creates the bean:
 */
@Configuration
@EnableConfigurationProperties({ R2dbcProperties.class, FlywayProperties.class })
public class FlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway(FlywayProperties flywayProperties, R2dbcProperties r2dbcProperties) {
        return Flyway.configure()
                .dataSource(
                        flywayProperties.getUrl(),
                        r2dbcProperties.getUsername(),
                        r2dbcProperties.getPassword()
                )
                .locations(flywayProperties.getLocations()
                        .stream()
                        .toArray(String[]::new))
                .baselineOnMigrate(true)
                .load();
    }

}
