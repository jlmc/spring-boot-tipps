package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.application.exception.ProviderClientErrorException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderRetriesExhaustedException;
import io.github.jlmc.maydayhlsprovider.application.exception.ProviderServerErrorException;
import io.github.jlmc.maydayhlsprovider.application.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<Map<String, String>> handleBadInput(ServerWebInputException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Invalid request parameters"));
    }

    // A non-429 4xx from the provider is not this API's caller's fault — it's an upstream
    // contract failure, so it is surfaced as 502 rather than the provider's original status.
    @ExceptionHandler({ProviderRetriesExhaustedException.class, ProviderServerErrorException.class, ProviderClientErrorException.class})
    public ResponseEntity<Map<String, String>> handleProviderFailure(Exception ex) {
        log.warn("Provider call failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "Provider search failed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.internalServerError().body(Map.of("error", "Unexpected error"));
    }
}
