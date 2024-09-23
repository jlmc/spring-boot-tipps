package io.github.jlmc.poc.adapters.ordersid;

import io.github.jlmc.poc.adapters.ordersid.control.local.OrderIdCreatorLocal;
import io.github.jlmc.poc.adapters.ordersid.control.remote.OrderIdCreatorRemote;
import io.github.jlmc.poc.adapters.ordersid.control.remote.OrderIdGeneratorServiceApiClient;
import io.github.jlmc.poc.domain.orders.ports.outgoing.OrderIdCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrdersIdConfiguration {

    @Bean
    OrderIdCreator orderIdCreator(OrdersIdConfigurationProperties properties, OrderIdGeneratorServiceApiClient orderIdGeneratorServiceApiClient) {
        return switch (properties.type()) {
            case LOCAL -> new OrderIdCreatorLocal();
            case REMOTE -> new OrderIdCreatorRemote(orderIdGeneratorServiceApiClient);
            case null, default ->
                    throw new IllegalArgumentException("Unsupported orders id type: " + properties.type());
        };
    }




}
