package io.github.jlmc.pizzacondo.om.service.adapter.outbound.persistence;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;
import io.github.jlmc.pizzacondo.om.service.domain.model.OrderStatus;
import io.github.jlmc.pizzacondo.om.service.domain.model.Topping;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class JpaOrderRepositoryTest {

    //@formatter:off
    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.3-alpine")
                    .withDatabaseName("pizza_condo_oms_db");
    //@formatter:on

    @Autowired
    ApplicationContext context;

    @Autowired
    JpaOrderRepository repository;



    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        //registry.add("spring.cloud.openfeign.client.config.products-service-client.url", wiremockServer::getBaseUrl);
        //registry.add("spring.cloud.openfeign.client.config.order-id-generator-service-client.url", wiremockServer::getBaseUrl);

        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void when_add_an_order_object() {

        Order order = Order.builder()
                .customerId("customer-1")
                .size(2)
                .status(OrderStatus.RECEIVED)
                .toppings(List.of(Topping.CHEESE, Topping.PEPPERONI, Topping.JALAPENO))
                .build();
        Order added = repository.add(order);

        Assertions.assertNotNull(added);
        Assertions.assertNotNull(added.getId());
        Assertions.assertNotNull(added.getPlacedAt());
    }
}