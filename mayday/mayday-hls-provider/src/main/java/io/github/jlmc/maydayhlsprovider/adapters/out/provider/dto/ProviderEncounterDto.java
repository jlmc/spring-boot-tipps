package io.github.jlmc.maydayhlsprovider.adapters.out.provider.dto;

import io.github.jlmc.maydayhlsprovider.domain.Encounter;

import java.time.Instant;

public record ProviderEncounterDto(String id, String visitType, Instant date, long durationSeconds) {

    public Encounter toDomain() {
        return new Encounter(id, visitType, date, durationSeconds);
    }
}
