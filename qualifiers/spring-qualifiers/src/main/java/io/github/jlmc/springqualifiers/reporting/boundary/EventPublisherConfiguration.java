package io.github.jlmc.springqualifiers.reporting.boundary;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventPublisherConfiguration {

    @Bean
    //@Qualifier("billing")
    public EventPublisher billing() {
        return operation -> {
            String s = "pushing Billing: " + operation;
            System.out.println(s);
            return s;
        };
    }

    @Bean
    @Qualifier("reporting")
    public EventPublisher reportingEventPublisher() {
        return operation -> {
            String s = "pushing Reporting: " + operation;
            System.out.println(s);
            return s;
        };
    }
}
