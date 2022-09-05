package io.github.jlmc.springbootkafkaexamplerx.interfaces.rest;

import io.github.jlmc.springbootkafkaexamplerx.application.commandservices.OrderBookingCommandService;
import io.github.jlmc.springbootkafkaexamplerx.domain.model.aggregates.OrderId;
import io.github.jlmc.springbootkafkaexamplerx.domain.model.commands.OrderBookingCommand;
import io.github.jlmc.springbootkafkaexamplerx.interfaces.rest.dtos.OrderRequest;
import io.github.jlmc.springbootkafkaexamplerx.interfaces.rest.dtos.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/orders")
public class OrdersController {

    //@Autowired
    //private KafkaTemplate<String, OrderBookedEvent> orderBookedEvents;

    private final OrderBookingCommandService orderBookingCommandService;

    public OrdersController(OrderBookingCommandService orderBookingCommandService) {
        this.orderBookingCommandService = orderBookingCommandService;
    }

    @GetMapping
    public Mono<String> hello() {
        return Mono.just("Hello " + System.currentTimeMillis());
    }

    @PostMapping
    public ResponseEntity<Mono<OrderResponse>> bookOrder(@RequestBody @Validated OrderRequest orderRequest) {

        Mono<OrderId> orderId = orderBookingCommandService.booking(new OrderBookingCommand(orderRequest.address(), orderRequest.item()));

        var body =
                orderId.map((OrderId it) ->
                        new OrderResponse(it.getId(), orderRequest.address(), orderRequest.item())
                ).flatMap(Mono::just);

        return ResponseEntity.ok(body);
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
