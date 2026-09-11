package io.github.jlmc.maydayhlsprovider.adapters.out.configuration;

import io.github.jlmc.maydayhlsprovider.application.port.out.AccountConcurrencyPolicy;
import io.github.jlmc.maydayhlsprovider.configuration.properties.AccountsProperties;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Configuration-based implementation of {@link AccountConcurrencyPolicy}. Future adapters
 * for the same port (database-backed, remote config, feature-flag-backed) can replace this
 * without any change to the use case — see README "Evolução futura".
 */
@Component
public class ConfigAccountConcurrencyPolicy implements AccountConcurrencyPolicy {

    private static final String DEFAULT_KEY = "default";

    private final AccountsProperties properties;

    public ConfigAccountConcurrencyPolicy(AccountsProperties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public int maxConcurrency(AccountId accountId) {
        return properties.concurrency().getOrDefault(accountId.value(), properties.concurrency().get(DEFAULT_KEY));
    }
}
