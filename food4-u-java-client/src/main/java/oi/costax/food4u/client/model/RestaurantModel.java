package oi.costax.food4u.client.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RestaurantModel {

    private Long id;
    private String name;
    private BigDecimal takeAwayTax = BigDecimal.ZERO;
    private boolean active;
}
