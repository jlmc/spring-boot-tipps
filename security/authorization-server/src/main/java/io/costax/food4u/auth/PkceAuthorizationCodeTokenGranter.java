package io.costax.food4u.auth;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Extension of the Authentication Grant Code strategy
 *
 * @see <a href="https://gist.github.com/thiagofa/daca4f4790b5b18fed800b83747127ca">Pkce Implementation Idie</a>
 * @see <a href="https://github.com/spring-projects/spring-security-oauth/pull/675/files">Pkce Implementation Idie</a>
 */
public class PkceAuthorizationCodeTokenGranter extends AuthorizationCodeTokenGranter {


    public PkceAuthorizationCodeTokenGranter(final AuthorizationServerTokenServices tokenServices,
                                             final AuthorizationCodeServices authorizationCodeServices,
                                             final ClientDetailsService clientDetailsService,
                                             final OAuth2RequestFactory requestFactory) {
        super(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory);
    }

    private static String generateHashSha256(String plainText) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(Utf8.encode(plainText));
            return Base64.encodeBase64URLSafeString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Authentication authentication = super.getOAuth2Authentication(client, tokenRequest);
        OAuth2Request request = authentication.getOAuth2Request();

        String codeChallenge = request.getRequestParameters().get("code_challenge");
        String codeChallengeMethod = request.getRequestParameters().get("code_challenge_method");
        String codeVerifier = request.getRequestParameters().get("code_verifier");

        if (codeChallenge != null || codeChallengeMethod != null) {
            if (codeVerifier == null) {
                throw new InvalidGrantException("Code verifier expected.");
            }

            if (!validateCodeVerifier(codeVerifier, codeChallenge, codeChallengeMethod)) {
                throw new InvalidGrantException(codeVerifier + " does not match expected code verifier.");
            }
        }

        return authentication;
    }

    private boolean validateCodeVerifier(final String codeVerifier,
                                         final String codeChallenge,
                                         final String codeChallengeMethod) {

        String generatedCodeChallenge;

        if ("plain".equalsIgnoreCase(codeChallengeMethod)) {
            generatedCodeChallenge = codeVerifier;
        } else if ("s256".equalsIgnoreCase(codeChallengeMethod)) {
            generatedCodeChallenge = generateHashSha256(codeVerifier);
        } else {
            throw new InvalidGrantException(codeChallengeMethod + " is not a valid challenge method.");
        }

        return generatedCodeChallenge.equals(codeChallenge);
    }

}
