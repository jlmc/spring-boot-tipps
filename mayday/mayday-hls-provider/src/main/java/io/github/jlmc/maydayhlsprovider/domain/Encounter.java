package io.github.jlmc.maydayhlsprovider.domain;

import java.time.Instant;

public record Encounter(String id, String visitType, Instant date, long durationSeconds) {
}
