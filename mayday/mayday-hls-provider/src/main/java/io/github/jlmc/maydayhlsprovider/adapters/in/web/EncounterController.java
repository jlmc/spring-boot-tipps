package io.github.jlmc.maydayhlsprovider.adapters.in.web;

import io.github.jlmc.maydayhlsprovider.application.port.in.AccountResolver;
import io.github.jlmc.maydayhlsprovider.application.port.in.SearchEncountersUseCase;
import io.github.jlmc.maydayhlsprovider.domain.AccountId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Objects;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

@RestController
@RequestMapping("/encounters")
public class EncounterController {

    private final SearchEncountersUseCase searchEncountersUseCase;
    private final AccountResolver accountResolver;

    public EncounterController(SearchEncountersUseCase searchEncountersUseCase, AccountResolver accountResolver) {
        this.searchEncountersUseCase = Objects.requireNonNull(searchEncountersUseCase);
        this.accountResolver = Objects.requireNonNull(accountResolver);
    }

    @GetMapping
    public Mono<ResponseEntity<EncounterResponse>> search(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam @DateTimeFormat(iso = DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DATE_TIME) Instant endDate,
            @RequestParam String visitType) {
        AccountId accountId = accountResolver.resolve(authorization);
        return searchEncountersUseCase.search(accountId, startDate, endDate, visitType)
                .map(EncounterResponse::found)
                .defaultIfEmpty(EncounterResponse.notFound())
                .map(ResponseEntity::ok);
    }
}
