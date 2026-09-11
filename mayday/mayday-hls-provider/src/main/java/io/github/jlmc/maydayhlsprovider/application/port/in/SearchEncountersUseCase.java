package io.github.jlmc.maydayhlsprovider.application.port.in;

import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.EncounterResult;
import reactor.core.publisher.Mono;

import java.time.Instant;

public interface SearchEncountersUseCase {

    /**
     * Emits the first matching encounter across all time windows, or completes empty when none match.
     */
    Mono<EncounterResult> search(AccountId accountId, Instant startDate, Instant endDate, String visitType);
}
