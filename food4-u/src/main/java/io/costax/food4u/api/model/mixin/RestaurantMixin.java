package io.costax.food4u.api.model.mixin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.costax.food4u.domain.model.PaymentMethod;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

public class RestaurantMixin {

    @JsonIgnore
    private OffsetDateTime createdAt;

   // @JsonIgnore
    private OffsetDateTime updatedAt;

    @JsonIgnore
    private Set<PaymentMethod> paymentMethods = new HashSet<>();

    @JsonIgnore
    private int version;
}
