package io.github.jlmc.uof.domain.fruits.ports.incoming;

import io.github.jlmc.uof.domain.fruits.entities.FruitFullDetailed;

import java.util.Optional;


public interface FruitFullDetailedReader {

    Optional<FruitFullDetailed> find(String productId);

}
