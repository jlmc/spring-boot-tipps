package io.github.jlmc.demo.kafka.interfaces.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.demo.kafka.interfaces.rest.dtos.incoming.OrderRequest;
import io.github.jlmc.demo.kafka.interfaces.rest.dtos.outgoing.OrderResponse;
import io.github.jlmc.demo.kafka.shareddomain.events.OrderBookedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrdersController {

    @Autowired
    private KafkaTemplate<Object, Object> template;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public String hello() {
        return "Hello " + System.currentTimeMillis();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> bookOrder(@RequestBody @Validated OrderRequest orderRequest) throws JsonProcessingException {
        OrderResponse order = new OrderResponse(UUID.randomUUID().toString(), orderRequest.address(), orderRequest.item());

        OrderBookedEvent event = OrderBookedEvent.of(order.id(), order.address(), orderRequest.item());
        String s = objectMapper.writeValueAsString(event);

        this.template.send("topic1", event.getId(), event);

        return ResponseEntity.ok(order);
    }
}
