package io.github.jlmc.maydayhlsprovider.application.port.in;

import io.github.jlmc.maydayhlsprovider.domain.AccountId;

/**
 * Translates an inbound authentication header into a domain {@link AccountId}.
 * Deliberately not backed by OAuth/JWT — this is a POC-level abstraction only.
 */
public interface AccountResolver {

    AccountId resolve(String authorizationHeaderValue);
}
