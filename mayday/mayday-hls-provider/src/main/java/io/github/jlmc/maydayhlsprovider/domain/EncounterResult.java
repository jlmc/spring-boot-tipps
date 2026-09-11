package io.github.jlmc.maydayhlsprovider.domain;

import java.time.Instant;

public record EncounterResult(String encounterId, Instant encounterDate, long durationSeconds) {

    public static EncounterResult from(Encounter encounter) {
        return new EncounterResult(encounter.id(), encounter.date(), encounter.durationSeconds());
    }
}
