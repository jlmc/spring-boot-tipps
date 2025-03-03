package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaStreamProcessor {

    @Bean
    public Consumer<OrderValidatedEvent> orderValidatedProcessor(OrderValidatedConsumer consumer) {
        return consumer::handler;
    }
}
