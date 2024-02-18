package io.github.jlmc.uof.domain.fruits.entities;

public record FruitFullDetailed(String id,
                                String name,
                                String description,
                                String origin,
                                PriceDetail priceDetail) {

    public FruitFullDetailed(Fruit fruit, PriceDetail priceDetail) {
        this(fruit.id(), fruit.name(), fruit.description(), fruit.origin(), priceDetail);
    }

}
