package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Function;

@Configuration
public class KafkaStreamProcessor {


    /*
    @Bean
    public Consumer<OrderPlacedEvent> processMessage() {
        return s -> {
            System.out.println(s);
        };
    }

     */

    @Bean
    public Function<OrderPlacedEvent, Message<OrderValidatedEvent>> validateOrderProcessor(OrderPlacedEventConsumer consumer) {
        return consumer::consume;
    }
}
