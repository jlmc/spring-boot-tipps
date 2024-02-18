package io.github.jlmc.uof.domain.fruits.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceDetail(String value,
                          String currency,
                          String sellerId,
                          Instant time,
                          String hash
) {
}