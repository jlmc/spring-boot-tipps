package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.application.exception.ProviderRetriesExhaustedException;
import io.github.jlmc.maydayhlsprovider.application.exception.UnauthorizedException;
import io.github.jlmc.maydayhlsprovider.application.port.in.AccountResolver;
import io.github.jlmc.maydayhlsprovider.application.port.in.SearchEncountersUseCase;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import io.github.jlmc.maydayhlsprovider.domain.EncounterResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest(EncounterController.class)
class EncounterControllerTest {

    private static final String QUERY = "startDate=2026-09-01T00:00:00Z&endDate=2026-09-10T00:00:00Z&visitType=FOLLOW_UP";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SearchEncountersUseCase searchEncountersUseCase;

    @MockitoBean
    private AccountResolver accountResolver;

    @Test
    void missingAuthorizationHeader_returns401() {
        when(accountResolver.resolve(null))
                .thenThrow(new UnauthorizedException("Missing or malformed Authorization header"));

        webTestClient.get().uri("/encounters?" + QUERY)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void malformedAuthorizationHeader_returns401() {
        when(accountResolver.resolve("Bearer "))
                .thenThrow(new UnauthorizedException("Missing or malformed Authorization header"));

        webTestClient.get().uri("/encounters?" + QUERY)
                .header("Authorization", "Bearer ")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void missingQueryParam_returns400() {
        webTestClient.get().uri("/encounters?startDate=2026-09-01T00:00:00Z&endDate=2026-09-10T00:00:00Z")
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void invalidDateFormat_returns400() {
        webTestClient.get().uri("/encounters?startDate=not-a-date&endDate=2026-09-10T00:00:00Z&visitType=FOLLOW_UP")
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void matchFound_returns200WithFoundTrue() {
        when(accountResolver.resolve("Bearer account-123")).thenReturn(new AccountId("account-123"));
        when(searchEncountersUseCase.search(any(AccountId.class), any(Instant.class), any(Instant.class), anyString()))
                .thenReturn(Mono.just(new EncounterResult("encounter-123", Instant.parse("2026-09-05T10:00:00Z"), 1800)));

        webTestClient.get().uri("/encounters?" + QUERY)
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isEqualTo(true)
                .jsonPath("$.encounterId").isEqualTo("encounter-123")
                .jsonPath("$.durationSeconds").isEqualTo(1800);
    }

    @Test
    void noMatchFound_returns200WithFoundFalse() {
        when(accountResolver.resolve("Bearer account-123")).thenReturn(new AccountId("account-123"));
        when(searchEncountersUseCase.search(any(AccountId.class), any(Instant.class), any(Instant.class), anyString()))
                .thenReturn(Mono.empty());

        webTestClient.get().uri("/encounters?" + QUERY)
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isEqualTo(false);
    }

    @Test
    void providerRetriesExhausted_returns502() {
        when(accountResolver.resolve("Bearer account-123")).thenReturn(new AccountId("account-123"));
        when(searchEncountersUseCase.search(any(AccountId.class), any(Instant.class), any(Instant.class), anyString()))
                .thenReturn(Mono.error(new ProviderRetriesExhaustedException(new RuntimeException("boom"))));

        webTestClient.get().uri("/encounters?" + QUERY)
                .header("Authorization", "Bearer account-123")
                .exchange()
                .expectStatus().isEqualTo(502);
    }
}
