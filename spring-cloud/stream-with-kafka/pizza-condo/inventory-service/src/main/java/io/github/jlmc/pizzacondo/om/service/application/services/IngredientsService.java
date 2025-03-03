package io.github.jlmc.pizzacondo.om.service.application.services;

import io.github.jlmc.pizzacondo.om.service.application.port.input.ValidateOrderUseCase;
import io.github.jlmc.pizzacondo.om.service.application.port.output.IngredientRepository;
import io.github.jlmc.pizzacondo.om.service.domain.model.IngredientType;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class IngredientsService implements ValidateOrderUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(IngredientsService.class);

    private final IngredientRepository ingredientRepository;


    @Override
    public boolean canBeSatisfied(ValidateOrderCommand command) {

        Map<IngredientType, Long> ingredients = getIngredients();

        // validate mass
        LOG.info("Validate MASS Ingredient {}", command.size());
        Long massQty = ingredients.get(IngredientType.MASS);
        if (massQty < command.size()) {
            LOG.warn("Invalid {} Ingredient {} required {}", IngredientType.MASS, massQty, command.size());
            return false;
        }

        Map<IngredientType, Long> toppings =
                command.toppings()
                .stream()
                .collect(Collectors.groupingBy(IngredientType::valueOf, Collectors.counting()));

        for (Map.Entry<IngredientType, Long> entry : toppings.entrySet()) {
            Long qty = ingredients.get(entry.getKey());

            LOG.info("Validating {} Ingredient {} required {}", entry.getKey(), qty, entry.getValue());

            if (qty < entry.getValue()) {
                LOG.warn("Invalid {} Ingredient {} required {}", entry.getKey(), qty, entry.getValue());
                return false;
            }
        }

        return true;
    }

    private Map<IngredientType, Long> getIngredients() {
        // ensure that all applications ingredients are in the map
        Map<IngredientType, Long> init = Arrays.stream(IngredientType.values())
                .map(it -> Map.entry(it, 0L))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        HashMap<IngredientType, Long> result = new HashMap<>(init);

        // add the existing quantities
        Map<IngredientType, Long> quantities = ingredientRepository.getQuantities();
        result.putAll(quantities);

        return result;
    }

}
