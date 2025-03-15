package io.github.jlmc.pizzacondo.inventory.service.adapter.inbound.messaging;

import io.github.jlmc.pizzacondo.common.messages.OrderStartedPreparationEvent;
import io.github.jlmc.pizzacondo.inventory.service.application.services.IngredientsService;
import io.github.jlmc.pizzacondo.inventory.service.domain.model.IngredientType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@Component
public class OrderStartedPreparationEventConsumer {

    private final IngredientsService ingredientsService;

    public void consume(OrderStartedPreparationEvent event) {
        log.info("Consuming the order started preparation event {}", event);

        Stream<Map.Entry<IngredientType, Long>> s1 = Stream.of(Map.entry(IngredientType.MASS, (long) event.size()));
        Stream<Map.Entry<IngredientType, Long>> s2 = event.toppings().stream().collect(Collectors.groupingBy(IngredientType::valueOf, Collectors.counting())).entrySet().stream();
        Map<IngredientType, Long> ingredients = Stream.concat(s1, s2).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ingredientsService.suppressIngredients(ingredients);
    }
}
