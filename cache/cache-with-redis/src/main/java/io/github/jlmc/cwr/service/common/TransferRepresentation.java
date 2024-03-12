package io.github.jlmc.cwr.service.common;

import java.io.Serializable;
import java.math.BigDecimal;

public record TransferRepresentation(String id, BigDecimal value, String details) implements Serializable {
}
