package io.github.jlmc.pizzacondo.inventory.service.application.port.output;

import io.github.jlmc.pizzacondo.inventory.service.domain.model.IngredientType;

import java.util.Map;

public interface IngredientRepository {

    Map<IngredientType, Long> getQuantities();

}
