package io.github.jlmc.poc;

import io.github.jlmc.poc.adapters.ordersid.OrdersIdConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(OrdersIdConfigurationProperties.class)
@SpringBootApplication
public class OpenfeignResilientCustomisationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenfeignResilientCustomisationsApplication.class, args);
    }

}
