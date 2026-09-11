package io.github.jlmc.maydayhlsprovider.domain;

import java.util.Objects;

public record AccountId(String value) {

    public AccountId {
        Objects.requireNonNull(value, "value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("AccountId value must not be blank");
        }
    }
}
