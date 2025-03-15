package io.github.jlmc.pizzacondo.om.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderFinishedPreparationEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderStartedPreparationEvent;
import io.github.jlmc.pizzacondo.common.messages.OrderValidatedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaStreamProcessor {

    @Bean
    public Consumer<OrderValidatedEvent> orderValidatedProcessor(OrderValidatedConsumer consumer) {
        return consumer::handler;
    }

    @Bean
    public Consumer<OrderStartedPreparationEvent> orderStartedPreparationProcessor(OrderStartedPreparationConsumer consumer) {
        return consumer::handler;
    }


    @Bean
    public Consumer<OrderFinishedPreparationEvent> orderFinishedPreparationEventProcessor(OrderFinishedPreparationConsumer consumer) {
        return consumer::handler;
    }
}
