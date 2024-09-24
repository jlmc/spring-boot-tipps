package io.github.jlmc.poc.adapters.ordersid.control.remote;

import io.github.jlmc.poc.domain.orders.entities.OrderId;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-id-generator-service-client", configuration = OrderIdGeneratorServiceApiClientConfiguration.class)
public interface OrderIdGeneratorServiceApiClient {

    @PostMapping("/api/orders/id")
    OrderId generateOrderId(@RequestBody Map<String, String> params);
}
