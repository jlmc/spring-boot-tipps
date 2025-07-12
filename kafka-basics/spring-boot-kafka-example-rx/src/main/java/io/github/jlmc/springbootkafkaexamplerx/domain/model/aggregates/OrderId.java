package io.github.jlmc.springbootkafkaexamplerx.domain.model.aggregates;

import java.util.Objects;

public class OrderId {
    private final String id;

    private OrderId(String id) {
        this.id = id;
    }

    public static OrderId createOrderId(String id) {
        return new OrderId(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderId orderId)) return false;

        return Objects.equals(id, orderId.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "<" + id + ">";
    }
}
