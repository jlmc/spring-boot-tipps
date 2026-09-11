package io.github.jlmc.maydayhlsprovider.configuration.properties;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

/**
 * Flat map binding: {@code mayday.accounts.concurrency.default}, {@code ...account-123}, etc.
 * "default" here is just a map key, not a Java identifier, so it does not clash with the keyword.
 * Validated eagerly (fail-fast at startup) rather than silently at request time.
 */
@ConfigurationProperties(prefix = "mayday.accounts")
@Validated
public record AccountsProperties(@NotEmpty Map<String, Integer> concurrency) {

    private static final String DEFAULT_KEY = "default";

    public AccountsProperties {
        if (concurrency != null) {
            if (!concurrency.containsKey(DEFAULT_KEY)) {
                throw new IllegalStateException("mayday.accounts.concurrency.default must be configured");
            }
            concurrency.forEach((account, value) -> {
                if (value == null || value <= 0) {
                    throw new IllegalStateException(
                            "mayday.accounts.concurrency." + account + " must be a positive integer, was " + value);
                }
            });
        }
    }
}
