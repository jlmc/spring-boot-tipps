package io.github.jlmc.cwr.service.configurations.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.cwr.service.configurations.AuthenticationFacade;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.type.format.jackson.JacksonJsonFormatMapper;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(
        auditorAwareRef = "auditorProvider",
        dateTimeProviderRef = "auditingDateTimeProvider"
)
public class PersistenceConfig {

    private static final String DEFAULT_USER = "anonymous";

    @Bean
    AuditorAware<String> auditorProvider(Optional<AuthenticationFacade> authenticationProvider) {
        return () -> {
            String userName =
                    authenticationProvider
                            .map(AuthenticationFacade::getUsername)
                            .orElse(DEFAULT_USER);

            return Optional.of(userName);
        };
    }

    @Bean
    public DateTimeProvider auditingDateTimeProvider(Clock clock) {
        return () -> Optional.of(Instant.now(clock));
    }

    /**
     * Hibernate Properties Customizer it is used to configure custom properties when it is necessary.
     * <br>
     * In this case, we are configuring the Json Formatter to serialize and deserialize the jsonb properties to use
     * the same instance of jackson ObjectMapper that is used in the application
     * There is also an open issue that is fixed in the version 6.3.0-Final, Please check
     * <a href="https://hibernate.atlassian.net/browse/HHH-17098">HHH-17098</a>
     *
     * @param objectMapper jackson Object mapper to be used to serialize and deserialize jsonb columns
     * @return an implementation of HibernatePropertiesCustomizer
     */
    @Bean
    HibernatePropertiesCustomizer customHibernatePropertiesCustomizer(ObjectMapper objectMapper) {
        return hibernateProperties -> hibernateProperties.put(
                AvailableSettings.JSON_FORMAT_MAPPER, new JacksonJsonFormatMapper(objectMapper)
        );
    }

}
