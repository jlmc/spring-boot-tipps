package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderAcceptedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaStreamProcessor {

    @Bean
    public Consumer<OrderAcceptedEvent> orderAcceptedProcessor(OrderAcceptedEventConsumer consumer) {
        return consumer::handler;
    }
}
