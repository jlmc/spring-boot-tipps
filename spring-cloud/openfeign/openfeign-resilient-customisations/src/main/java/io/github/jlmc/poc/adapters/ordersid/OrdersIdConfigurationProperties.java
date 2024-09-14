package io.github.jlmc.poc.adapters.ordersid;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("poc.orders-id")
public record OrdersIdConfigurationProperties(@NotNull OrdersIdConfigurationProperties.Type type) {

    @ConstructorBinding
    public OrdersIdConfigurationProperties {
        if (type == null) {
            throw new IllegalStateException("OrdersIdConfigurationProperties type cannot be null");
        }
    }

    enum Type {
        /**
         * The order id will use the local generator implementation.
         */
        LOCAL,
        /**
         * The order id will use the remote generator implementation, that will be accessed by HTTP.
         */
        REMOTE
    }
}
