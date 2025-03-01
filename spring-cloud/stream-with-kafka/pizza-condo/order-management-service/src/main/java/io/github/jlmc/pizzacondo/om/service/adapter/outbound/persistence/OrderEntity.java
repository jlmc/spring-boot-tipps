package io.github.jlmc.pizzacondo.om.service.adapter.outbound.persistence;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import io.github.jlmc.pizzacondo.om.service.domain.model.Topping;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.*;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orders_seq")
    @SequenceGenerator(name = "orders_seq", sequenceName = "orders_id_seq", allocationSize = 5, initialValue = 1)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "size_of", nullable = false)
    private int size = 1;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "toppings", columnDefinition = "jsonb", updatable = false)
    private List<Topping> toppings = new ArrayList<>();


   // @org.springframework.data.annotation.CreatedDate
    @CreationTimestamp
    @Column(name = "placed_at")
    private Instant placedAt;

    @CreatedDate
    Instant insertedAt;

    @CreatedBy
    String insertedBy;

    @LastModifiedDate
    Instant lastUpdatedAt;

    @LastModifiedBy
    String lastUpdatedBy;

    public OrderEntity() {
    }

    public OrderEntity(Order order) {
        this.id = Optional.ofNullable(order.getId()).map(Long::parseLong).orElse(null);
        this.customerId = order.getCustomerId();
        this.status = order.getStatus();
        this.size = order.getSize();
        var toppings1  = Optional.ofNullable(order.getToppings())
                .stream()
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toList();
        this.toppings.addAll(toppings1);
    }
}
