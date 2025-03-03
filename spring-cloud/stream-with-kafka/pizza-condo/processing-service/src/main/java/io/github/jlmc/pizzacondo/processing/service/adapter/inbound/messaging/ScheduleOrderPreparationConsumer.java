package io.github.jlmc.pizzacondo.processing.service.adapter.inbound.messaging;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ScheduleOrderPreparationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ScheduleOrderPreparationConsumer.class);

    public void consume(ScheduleOrderPreparationEvent event) {

    }
}
