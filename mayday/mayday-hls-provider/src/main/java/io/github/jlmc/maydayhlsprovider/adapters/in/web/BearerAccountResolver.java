package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.application.exception.UnauthorizedException;
import io.github.jlmc.maydayhlsprovider.application.port.in.AccountResolver;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import org.springframework.stereotype.Component;

/**
 * Interprets {@code Authorization: Bearer account-123} as the literal AccountId. No OAuth,
 * no JWT validation, no identity provider — a deliberate POC-level simplification.
 */
@Component
public class BearerAccountResolver implements AccountResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public AccountId resolve(String authorizationHeaderValue) {
        if (authorizationHeaderValue == null
                || !authorizationHeaderValue.startsWith(BEARER_PREFIX)
                || authorizationHeaderValue.length() == BEARER_PREFIX.length()) {
            throw new UnauthorizedException("Missing or malformed Authorization header");
        }
        String token = authorizationHeaderValue.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            throw new UnauthorizedException("Missing or malformed Authorization header");
        }
        return new AccountId(token);
    }
}
