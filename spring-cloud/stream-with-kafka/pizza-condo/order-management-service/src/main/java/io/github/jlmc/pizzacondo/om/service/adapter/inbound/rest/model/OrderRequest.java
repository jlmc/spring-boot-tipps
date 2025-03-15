package io.github.jlmc.pizzacondo.om.service.adapter.inbound.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.jlmc.pizzacondo.om.service.application.port.input.PlaceOrderUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderRequest(@NotBlank String customerId,
                           @Positive int size,
                           @Size(min = 1, max = 10) List<@Pattern(regexp = "PEPPERONI|CHEESE|JALAPENO") String> toppings) {

    public PlaceOrderUseCase.PlaceOrderCommand toCommand() {
        return new PlaceOrderUseCase.PlaceOrderCommand(customerId, size, toppings);
    }
}
