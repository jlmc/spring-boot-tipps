package io.github.jlmc.maydayhlsprovider.application.port.out;

import io.github.jlmc.maydayhlsprovider.domain.AccountId;

/**
 * Resolves the maximum number of in-flight provider requests allowed for one search,
 * per account. Not a rate limiter, and not a global/distributed limit — see README.
 */
public interface AccountConcurrencyPolicy {

    int maxConcurrency(AccountId accountId);
}
