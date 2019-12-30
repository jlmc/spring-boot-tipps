package io.costax.food4u.domain.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String code;

    @PositiveOrZero
    private BigDecimal subTotal = BigDecimal.ZERO;
    @PositiveOrZero
    private BigDecimal takeAwayTax = BigDecimal.ZERO;
    @PositiveOrZero
    private BigDecimal totalValue = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.CREATED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "user_client_id", nullable = false)
    private User client;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "request_id", nullable = false, updatable = false)
    private List<RequestItem> items = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street", column = @Column(name = "delivery_address_street")),
            @AttributeOverride(name = "city", column = @Column(name = "delivery_address_city")),
            @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_address_zip_code"))
    })
    private Address deliveryAddress;

    private OffsetDateTime canceledAt;
    private OffsetDateTime confirmedAt;
    private OffsetDateTime deliveryAt;


    @Version
    private int version;

    public static Request of(final User client, final Restaurant restaurant, final PaymentMethod paymentMethod, final Address deliveryAddress) {

        final Request request = new Request();
        request.client = client;
        request.restaurant = restaurant;
        request.paymentMethod = paymentMethod;
        request.deliveryAddress = deliveryAddress;

        return request;
    }

    public Request addItem(final Product product, final Integer qty, final String observations) {
        return this.addItem(RequestItem.of(product, qty, observations));
    }

    private Request addItem(final RequestItem requestItem) {
        this.items.add(requestItem);
        this.subTotal = this.subTotal.add(requestItem.getTotalPrice());
        this.takeAwayTax = restaurant.getTakeAwayTax();
        this.totalValue = this.subTotal.add(restaurant.getTakeAwayTax());

        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Request)) return false;
        final Request request = (Request) o;
        return getId() != null && Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @PrePersist
    void prePersist() {
        this.code = UUID.randomUUID().toString();
    }

    public Request confirm(Clock clock) {
        if (RequestStatus.CONFIRMED == this.status) {
            return this;
        }

        if (this.status != RequestStatus.CREATED) {
            throw new IllegalStateException();
        }
        this.status = RequestStatus.CONFIRMED;

        this.confirmedAt = OffsetDateTime.now(clock);

        return this;
    }

    public Request cancel(Clock clock) {
        if (RequestStatus.CANCELED == this.status) {
            return this;
        }

        if (this.status != RequestStatus.CREATED) {
            throw new IllegalStateException();
        }
        this.status = RequestStatus.CANCELED;
        this.canceledAt = OffsetDateTime.now(clock);

        return this;
    }

    public Request delivery(Clock clock) {
        if (RequestStatus.DELIVERED == this.status) {
            return this;
        }

        if (this.status != RequestStatus.CONFIRMED) {
            throw new IllegalStateException();
        }
        this.status = RequestStatus.DELIVERED;
        this.deliveryAt = OffsetDateTime.now(clock);

        return this;
    }

    public enum RequestStatus {
        CREATED, CONFIRMED, CANCELED, DELIVERED
    }
}