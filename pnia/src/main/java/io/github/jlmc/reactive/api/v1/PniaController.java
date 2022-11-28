package io.github.jlmc.reactive.api.v1;

import io.github.jlmc.reactive.domain.usecases.GetPhoneNumberAggregation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@Slf4j
@RequestMapping(path = "aggregate")

public class PniaController {

    private final GetPhoneNumberAggregation aggregation;

    public PniaController(GetPhoneNumberAggregation aggregation) {
        this.aggregation = aggregation;
    }

    @PostMapping(
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE

    )
    public Mono<Map<String, Map<String, Long>>> createAggregation(
            @RequestBody @Validated List<String> phoneNumbers) {

        return aggregation.execute(phoneNumbers);
    }
}
