package io.github.jlmc.demo.kafka.interfaces.rest;

import io.github.jlmc.demo.kafka.interfaces.rest.dtos.incoming.OrderRequest;
import io.github.jlmc.demo.kafka.interfaces.rest.dtos.outgoing.OrderResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    public String hello() {
        return "Hello " + System.currentTimeMillis();
    }

    @PostMapping
    public ResponseEntity<OrderResponse> bookOrder(@RequestBody @Validated OrderRequest orderRequest) {
        return ResponseEntity.ok(new OrderResponse(UUID.randomUUID().toString(), orderRequest.address(), orderRequest.item()));
    }
}
