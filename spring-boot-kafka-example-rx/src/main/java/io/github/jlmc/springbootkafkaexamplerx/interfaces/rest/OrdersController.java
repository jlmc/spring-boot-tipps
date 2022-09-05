package io.github.jlmc.springbootkafkaexamplerx.interfaces.rest;

import io.github.jlmc.springbootkafkaexamplerx.interfaces.rest.dtos.OrderRequest;
import io.github.jlmc.springbootkafkaexamplerx.interfaces.rest.dtos.OrderResponse;
import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.OrderBookedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(path = "/orders")
public class OrdersController {

    @Autowired
    private KafkaTemplate<String, OrderBookedEvent> orderBookedEvents;

    @GetMapping
    public Mono<String> hello() {
        return Mono.just("Hello " + System.currentTimeMillis());
    }

    @PostMapping
    public ResponseEntity<Mono<OrderResponse>> bookOrder(@RequestBody @Validated OrderRequest orderRequest) {
        OrderResponse order = new OrderResponse(UUID.randomUUID().toString(), orderRequest.address(), orderRequest.item());

        OrderBookedEvent event = OrderBookedEvent.of(order.id(), order.address(), orderRequest.item());
        //String s = objectMapper.writeValueAsString(event);

        orderBookedEvents.send("ORDERS_1", event.getId(), event);

        return ResponseEntity.ok(Mono.just(order));
    }

    /*
    docker exec -it platform-kafka  /bin/bash
    ORDERS_1
    kafka-console-consumer --bootstrap-server 127.0.0.1:9092 --topic ORDERS_1 --from-beginning --property print.key=true --property key.separator=

    docker exec -it billing-kafka  /bin/bash
    kafka-console-consumer --bootstrap-server 127.0.0.1:5152 --topic BILLING_TOPIC --from-beginning --property print.key=true --property key.separator=
    BILLING_TOPIC
     */

}
