package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderPlacedEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderStartedPreparationEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderValidatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;
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

    @Bean
    public Consumer<OrderStartedPreparationEvent> orderStartedPreparationProcessor(OrderStartedPreparationEventConsumer consumer) {
        return consumer::consume;
    }
}
