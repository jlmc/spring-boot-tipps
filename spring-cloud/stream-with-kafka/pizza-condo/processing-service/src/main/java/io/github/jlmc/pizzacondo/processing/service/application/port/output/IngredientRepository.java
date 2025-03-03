package io.github.jlmc.pizzacondo.processing.service.application.port.output;

import io.github.jlmc.pizzacondo.processing.service.domain.model.IngredientType;

import java.util.Map;

public interface IngredientRepository {

    Map<IngredientType, Long> getQuantities();

}
