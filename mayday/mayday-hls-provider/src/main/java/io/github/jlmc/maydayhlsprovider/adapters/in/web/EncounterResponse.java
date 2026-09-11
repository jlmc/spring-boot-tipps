package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.domain.EncounterResult;

import java.time.Instant;

/**
 * HTTP response DTO, distinct from the domain {@link EncounterResult} so the domain is never
 * leaked over HTTP. The API never responds 404 or an empty body for "no match" — it always
 * responds 200 with {@code found} telling the caller whether a match was located.
 */
public record EncounterResponse(boolean found, String encounterId, Instant encounterDate, Long durationSeconds) {

    public static EncounterResponse found(EncounterResult result) {
        return new EncounterResponse(true, result.encounterId(), result.encounterDate(), result.durationSeconds());
    }

    public static EncounterResponse notFound() {
        return new EncounterResponse(false, null, null, null);
    }
}
