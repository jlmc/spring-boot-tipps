package io.github.jlmc.springbootkafkaexamplerx.application.commandservices;

import io.github.jlmc.springbootkafkaexamplerx.application.outboundservices.OrderReported;
import io.github.jlmc.springbootkafkaexamplerx.domain.model.aggregates.OrderId;
import io.github.jlmc.springbootkafkaexamplerx.domain.model.commands.OrderBookingCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class OrderBookingCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBookingCommandService.class);

    @OrderReported(system = "demo")
    public Mono<OrderId> booking(OrderBookingCommand command) {

        OrderId orderId = OrderId.createOrderId(UUID.randomUUID().toString());

        LOGGER.info("Booking the order ... <{}>", orderId);

        return Mono.just(orderId);
    }
}
