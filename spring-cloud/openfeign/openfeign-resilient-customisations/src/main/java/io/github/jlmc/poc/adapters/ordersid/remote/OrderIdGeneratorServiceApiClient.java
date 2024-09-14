package io.github.jlmc.poc.adapters.ordersid.remote;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-id-generator-service-client", configuration = OrderIdGeneratorServiceApiClientConfiguration.class)
public interface OrderIdGeneratorServiceApiClient {

    @PostMapping("/api/orders/id")
    OrderId generateOrderId();
}
