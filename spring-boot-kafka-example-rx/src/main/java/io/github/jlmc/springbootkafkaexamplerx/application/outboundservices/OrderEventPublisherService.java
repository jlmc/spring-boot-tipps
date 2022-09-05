package io.github.jlmc.springbootkafkaexamplerx.application.outboundservices;

import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.OrderBookedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisherService {

    private final KafkaTemplate<String, OrderBookedEvent> orderBookedEvents;

    public OrderEventPublisherService(KafkaTemplate<String, OrderBookedEvent> orderBookedEvents) {
        this.orderBookedEvents = orderBookedEvents;
    }

    public void send(OrderBookedEvent event) {
        orderBookedEvents.send("ORDERS_1", event.getId(), event);
    }
}
