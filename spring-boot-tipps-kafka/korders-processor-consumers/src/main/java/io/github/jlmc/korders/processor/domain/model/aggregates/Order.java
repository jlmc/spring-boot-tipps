package io.github.jlmc.korders.processor.domain.model.aggregates;

import io.github.jlmc.korders.processor.domain.model.entities.OrderItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@Table(name = "orders")
public class Order implements Persistable {
    @Id
    private String id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    private Instant orderCreated;

    @CreationTimestamp
    private Instant insertedAt;
    @UpdateTimestamp
    private Instant updatedAt;

    @Version
    private Integer version;

    @Builder.Default
    @OneToMany(
            fetch = FetchType.LAZY,
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            cascade = {
                    PERSIST,
                    //REMOVE,
                    DETACH,
                    MERGE
            })
    @JoinColumn(name = "order_id", nullable = false, updatable = false) // join column is in table for Order
    private Set<OrderItem> items = new HashSet<>();

    protected Order() {
    }

    @Transient
    public Order addItem(OrderItem item) {
        if (this.items == null) this.items = new HashSet<>();
        this.items.add(item);
        return this;
    }

    @Transient
    public Order replaceItems(Collection<OrderItem> items) {
        if (this.items == null) this.items = new HashSet<>();
        this.items.clear();
        this.items.addAll(items);
        return this;
    }

    @Override
    public boolean isNew() {
        return this.insertedAt == null || version == null;
    }

    public enum Status {
        NEW,
        IN_PREPARATION,
        DELIVEREDING,
        DELIVERED,
        CANCELED,
    }
}
