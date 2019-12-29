package io.costax.food4u.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Request {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String code;

    @PositiveOrZero
    private BigDecimal subTotal = BigDecimal.ZERO;
    @PositiveOrZero
    private BigDecimal takeAwayTax = BigDecimal.ZERO;;
    @PositiveOrZero
    private BigDecimal totalValue = BigDecimal.ZERO;;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.CREATED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime created_at;

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

    public enum RequestStatus {
        CREATED, CONFIRMED, CANCELED, DELIVERED
    }

    @PrePersist
    void prePersist() {
        this.code = UUID.randomUUID().toString();
    }
}