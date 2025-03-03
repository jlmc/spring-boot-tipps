package io.github.jlmc.pizzacondo.processing.service.config.persistence;


import com.fasterxml.jackson.databind.ObjectMapper;
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

import static io.github.jlmc.pizzacondo.processing.service.config.persistence.CurrentUserProvider.DEFAULT_USER;

@Configuration
@EnableJpaAuditing(
        auditorAwareRef = "auditorProvider",
        dateTimeProviderRef = "auditingDateTimeProvider"
)
public class PersistenceConfiguration {

    /**
     * Auditor provider auditor aware.
     *
     * @return the auditor aware
     */
    @Bean
    AuditorAware<String> auditorProvider(Optional<CurrentUserProvider> currentUserProvider) {
        return () -> {
            String userName =
                    currentUserProvider
                            .map(CurrentUserProvider::getCurrentUserName)
                            .orElse(DEFAULT_USER);

            return Optional.of(userName);
        };
    }

    /**
     * Auditing date time provider date time provider.
     *
     * @return the date time provider
     */
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

