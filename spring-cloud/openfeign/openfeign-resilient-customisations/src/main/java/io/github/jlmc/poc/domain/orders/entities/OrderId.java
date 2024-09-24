package io.github.jlmc.poc.domain.orders.entities;

public record OrderId(String id) {

    @Override
    public String toString() {
        return id();
    }
}
