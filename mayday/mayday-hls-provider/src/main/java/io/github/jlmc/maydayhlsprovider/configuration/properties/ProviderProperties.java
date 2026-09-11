package io.github.jlmc.maydayhlsprovider.configuration.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties(prefix = "mayday.provider")
@Validated
public record ProviderProperties(
        @NotBlank String baseUrl,
        @NotNull @Valid SearchProperties search,
        @NotNull @Valid HttpProperties http,
        @NotNull @Valid RetryProperties retry) {

    public record SearchProperties(@NotNull Duration windowSize) {
        public SearchProperties {
            if (windowSize != null && (windowSize.isZero() || windowSize.isNegative())) {
                throw new IllegalArgumentException("mayday.provider.search.window-size must be positive");
            }
        }
    }

    public record HttpProperties(@NotNull Duration connectTimeout, @NotNull Duration responseTimeout) {
    }

    public record RetryProperties(@Min(1) int maxAttempts, @NotNull @Valid Backoff backoff) {

        public record Backoff(@NotNull Duration minBackoff,
                               @NotNull Duration maxBackoff,
                               @DecimalMin("0.0") @DecimalMax("1.0") double jitterFactor) {
            public Backoff {
                if (minBackoff != null && maxBackoff != null && minBackoff.compareTo(maxBackoff) > 0) {
                    throw new IllegalArgumentException("minBackoff must be <= maxBackoff");
                }
            }
        }
    }
}
