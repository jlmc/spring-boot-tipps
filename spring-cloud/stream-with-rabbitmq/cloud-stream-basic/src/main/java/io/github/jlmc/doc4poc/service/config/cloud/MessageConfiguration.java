package io.github.jlmc.doc4poc.service.config.cloud;

import io.github.jlmc.doc4poc.service.infrastructure.messaging.OrderChanged;
import io.github.jlmc.doc4poc.service.infrastructure.messaging.ReportChangesListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageConfiguration {

    @Bean
    public Consumer<OrderChanged> reportChangesListener() {
        return new ReportChangesListener();
    }


}
