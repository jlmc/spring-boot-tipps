package io.github.jlmc.pizzacondo.inventory.service.adapter.outbound.persistence;

import io.github.jlmc.pizzacondo.inventory.service.application.port.output.IngredientRepository;
import io.github.jlmc.pizzacondo.inventory.service.domain.model.IngredientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface JpaIngredientRepository extends JpaRepository<IngredientEntity, Long>, IngredientRepository {

    @Override
    default Map<IngredientType, Long> getQuantities() {
        return findAll().stream()
                .collect(Collectors.toMap(it -> IngredientType.valueOf(it.getName()), it -> (long) it.getQty()));
    }

    Optional<IngredientEntity> findByName(String name);

    @Override
    default void suppressQty(String name, Long value) {
        Optional<IngredientEntity> ingredient = findByName(name);
        ingredient.ifPresent(it -> it.suppressQty(value));
    }
}
