package io.costax.examplesapi.di.control;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfiguration {

    @Bean
    NotifierEmail notifierEmail() {
        // FIXME: 04/12/2019 - the values should come from configuration
        return new NotifierEmail("smtp.dummy.io", 5);
    }
}
