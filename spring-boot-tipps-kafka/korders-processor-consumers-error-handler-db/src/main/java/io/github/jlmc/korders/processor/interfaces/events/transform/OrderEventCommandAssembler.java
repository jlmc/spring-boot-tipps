package io.github.jlmc.korders.processor.interfaces.events.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.shareddomain.events.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderEventCommandAssembler {

    private final ObjectMapper objectMapper;

    public OrderEventCommandAssembler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RegisterNewOrderCommand toCommand(ConsumerRecord<String, String> consumerRecord) {
        String value = consumerRecord.value();

        OrderEvent orderEvent = readEvent(value);
        String orderId = orderEvent.getOrderId();
        Instant instant = orderEvent.getInstant();
        List<Pair<String, Integer>> items =
                orderEvent.getItems()
                          .stream()
                          .map(p -> Pair.of(p.getProductId(), p.getQty()))
                          .toList();

        return new RegisterNewOrderCommand(orderId, instant, items);
    }

    public OrderEvent readEvent(String json) {
        try {
            return objectMapper.readValue(json, OrderEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
