package io.costax.springemaildemos.domain.orders.control;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
@ToString
@Configuration
@ConfigurationProperties(prefix = "demoapp.orders")
public class OrdersConfigurationProperties {

    @NotNull
    @Getter
    @Setter
    private Emails emails = new Emails();

    @Getter
    @Setter
    public static class Emails {

        @NotNull
        private String from;

    }
}
