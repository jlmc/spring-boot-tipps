package io.github.jlmc.poc.domain.invoices.entities;

import java.math.BigDecimal;
import java.time.Instant;

public record Invoice(String number, Instant date, BigDecimal amount, byte[] fileContent) {
}
