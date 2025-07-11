package io.github.jlmc.uploadcsv.adapters.in.rest;

import io.github.jlmc.uploadcsv.adapters.in.rest.errors.ApiErrorsConfigurationProperties;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.ControllerAdvice;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers.ConstraintViolationExceptionHandler;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers.CsvConstraintViolationsExceptionHandler;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers.CsvIllegalDataExceptionHandler;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers.MethodArgumentNotValidExceptionHandler;
import io.github.jlmc.uploadcsv.adapters.in.rest.errors.handlers.WebExchangeBindExceptionHandler;
import io.github.jlmc.uploadcsv.adapters.in.rest.resources.PalindromeRequest;
import io.github.jlmc.uploadcsv.application.services.PalindromicVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(
        classes = {
                PalindromeController.class,
                ConstraintViolationExceptionHandler.class,
                WebExchangeBindExceptionHandler.class,
                MethodArgumentNotValidExceptionHandler.class,
                CsvConstraintViolationsExceptionHandler.class,
                CsvIllegalDataExceptionHandler.class,
                ApiErrorsConfigurationProperties.class,
                ControllerAdvice.class,
        }
)
class PalindromeControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PalindromicVerifier verifier;

    @Test
    @DisplayName("should return true when input is a palindrome")
    void shouldReturnTrueForPalindrome() {
        PalindromeRequest request = new PalindromeRequest("Madam");
        when(verifier.isPalindrome("Madam")).thenReturn(true);

        webTestClient.post()
                .uri("/palindromes/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.palindrome").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("It's a palindrome!");
    }

    @Test
    @DisplayName("should return false when input is not a palindrome")
    void shouldReturnFalseForNonPalindrome() {
        PalindromeRequest request = new PalindromeRequest("Hello");
        when(verifier.isPalindrome("Hello")).thenReturn(false);

        webTestClient.post()
                .uri("/palindromes/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.palindrome").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("Not a palindrome.");
    }

    @Test
    @DisplayName("should return 400 when input is blank")
    void shouldReturnBadRequestForBlankText() {
        PalindromeRequest request = new PalindromeRequest("   ");

        webTestClient.post()
                .uri("/palindromes/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("XXXX4000")
                .jsonPath("$.message").isEqualTo("Request cannot be processed due to validation errors")
                .jsonPath("$.fields[0].description").isEqualTo("Text must not be blank")
                .jsonPath("$.fields[0].name").isEqualTo("text");
    }
}
