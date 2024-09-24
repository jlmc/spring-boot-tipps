package io.github.jlmc.poc.domain.orders.entities;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class Order{

    private final OrderId id;
    private final BigDecimal totalPrice;
    private final Set<OrderItem> items = new LinkedHashSet<>();

    public Order(Order.Builder builder) {
        this.id = builder.orderId;
        this.totalPrice = builder.totalPrice;
        this.items.addAll(builder.itemMap.values());
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Set<OrderItem> getItems() {
        return Set.copyOf(items);
    }

    @Override
    public String toString() {
        return "Order{" +
               "id=" + id +
               ", totalPrice=" + totalPrice +
               '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public OrderId getId() {
        return id;
    }

    public static class Builder {
        private OrderId orderId;
        private BigDecimal totalPrice = BigDecimal.ZERO;
        private final Map<Product, OrderItem> itemMap = new HashMap<>();

        public Order build() {
            return new Order(this);
        }

        public Builder id(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder addItem(Product product, Integer quantity) {
            OrderItem newOrderItem;

            if (itemMap.containsKey(product)) {
                OrderItem oldOrderItem = itemMap.get(product);

                this.totalPrice = this.totalPrice.subtract(oldOrderItem.totalPrice());

                newOrderItem = OrderItem.orderItem(quantity + oldOrderItem.quantity(), product);

            } else {
                newOrderItem = OrderItem.orderItem(quantity, product);
            }

            itemMap.put(product, newOrderItem);
            this.totalPrice = this.totalPrice.add(newOrderItem.totalPrice());

            return this;
        }
    }
}
