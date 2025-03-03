package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class KafkaStreamProcessor {

    @Bean
    public Consumer<ScheduleOrderPreparationEvent> scheduleOrderPreparationProcessor(ScheduleOrderPreparationConsumer consumer) {
        return consumer::consume;
    }
}
