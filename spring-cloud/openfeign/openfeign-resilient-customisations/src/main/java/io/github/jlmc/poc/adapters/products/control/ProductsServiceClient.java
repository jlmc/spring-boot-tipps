package io.github.jlmc.poc.adapters.products.control;

import io.github.jlmc.poc.domain.orders.entities.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "products-service-client", configuration = ProductsServiceClientConfiguration.class)
public interface ProductsServiceClient {

    @GetMapping("/api/products/{id}")
    Product product(@PathVariable("id") Integer productId);
}
