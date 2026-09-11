package io.github.jlmc.maydayhlsprovider.adapters.out.configuration;

import io.github.jlmc.maydayhlsprovider.configuration.properties.AccountsProperties;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigAccountConcurrencyPolicyTest {

    @Test
    void knownAccount_returnsConfiguredValue() {
        var properties = new AccountsProperties(Map.of("default", 5, "account-123", 3, "account-456", 10));
        var policy = new ConfigAccountConcurrencyPolicy(properties);

        assertThat(policy.maxConcurrency(new AccountId("account-123"))).isEqualTo(3);
        assertThat(policy.maxConcurrency(new AccountId("account-456"))).isEqualTo(10);
    }

    @Test
    void unknownAccount_returnsDefault() {
        var properties = new AccountsProperties(Map.of("default", 5, "account-123", 3));
        var policy = new ConfigAccountConcurrencyPolicy(properties);

        assertThat(policy.maxConcurrency(new AccountId("account-999"))).isEqualTo(5);
    }

    @Test
    void missingDefaultKey_failsFastAtConstruction() {
        Map<String, Integer> withoutDefault = Map.of("account-123", 3);

        assertThatThrownBy(() -> new AccountsProperties(withoutDefault))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("default");
    }

    @Test
    void zeroOrNegativeValue_failsFastAtConstruction() {
        Map<String, Integer> zero = new HashMap<>();
        zero.put("default", 5);
        zero.put("account-123", 0);

        Map<String, Integer> negative = new HashMap<>();
        negative.put("default", 5);
        negative.put("account-456", -1);

        assertThatThrownBy(() -> new AccountsProperties(zero)).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new AccountsProperties(negative)).isInstanceOf(IllegalStateException.class);
    }
}
