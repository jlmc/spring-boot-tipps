package io.github.jlmc.korders.processor.domain.model.commands;

import org.springframework.data.util.Pair;

import java.time.Instant;
import java.util.List;

public record RegisterNewOrderCommand(String orderId, Instant created, List<Pair<String, Integer>> items) {
}
