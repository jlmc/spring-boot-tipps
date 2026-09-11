package io.github.jlmc.maydayhlsprovider.adapters.out.provider;

import io.github.jlmc.maydayhlsprovider.domain.Encounter;

import java.time.Duration;
import java.util.List;

/**
 * Closed set of possible outcomes of one HTTP call to the provider, translated from the raw
 * {@code ClientResponse} before any retry/error decision is made. Exhaustively handled via
 * pattern-matching {@code switch} in {@link WebClientProviderEncounterSearchAdapter}.
 */
sealed interface ProviderCallOutcome {

    record Success(List<Encounter> encounters) implements ProviderCallOutcome {
    }

    /** {@code retryAfter} is {@code null} when the provider did not supply a {@code Retry-After} header. */
    record RateLimited(Duration retryAfter) implements ProviderCallOutcome {
    }

    record ServerError(int status) implements ProviderCallOutcome {
    }

    record ClientError(int status) implements ProviderCallOutcome {
    }
}
