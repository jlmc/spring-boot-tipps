package io.github.jlmc.poc.domain.orders.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(Integer id, String name, BigDecimal price, String description) {
}
