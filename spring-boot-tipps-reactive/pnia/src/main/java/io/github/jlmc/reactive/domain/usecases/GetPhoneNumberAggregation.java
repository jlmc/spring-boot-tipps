package io.github.jlmc.reactive.domain.usecases;

import io.github.jlmc.reactive.domain.model.PhoneNumber;
import io.github.jlmc.reactive.domain.services.blacklist.BlackListService;
import io.github.jlmc.reactive.domain.services.blacklist.ForbiddenInputValuesException;
import io.github.jlmc.reactive.domain.services.prefixes.PrefixesService;
import io.github.jlmc.reactive.domain.services.sectors.BusinessSectorService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Service
public class GetPhoneNumberAggregation {

    private final PrefixesService prefixesService;
    private final BusinessSectorService businessSectorService;
    private final BlackListService blackListService;

    public GetPhoneNumberAggregation(PrefixesService prefixesService,
                                     BusinessSectorService businessSectorService,
                                     BlackListService blackListService) {
        this.prefixesService = prefixesService;
        this.businessSectorService = businessSectorService;
        this.blackListService = blackListService;
    }

    public Mono<Map<String, Map<String, Long>>> execute(List<String> phoneNumbersAsText) {

        Collection<String> forbiddenInputs = blackListService.getForbiddenInputs(phoneNumbersAsText);
        if (!forbiddenInputs.isEmpty()) {
            throw new ForbiddenInputValuesException(forbiddenInputs);
        }

        Stream<String> nonNullPhoneNumbers = phoneNumbersAsText.stream().filter(Objects::nonNull);

        Flux<PhoneNumber> flux =
                Flux.fromStream(nonNullPhoneNumbers)
                    .filter(PhoneNumber::isValidPhoneNumber)
                    .map(PhoneNumber::of)
                    .flatMap(this::prefixOf)
                    .flatMap(phoneNumber -> businessSectorService.getNumberSector(phoneNumber)
                                                                 .filter(Objects::nonNull)
                                                                 .map(phoneNumber::withSector));

        return flux
                .collect(
                        Collectors.groupingBy(PhoneNumber::getPrefix,
                                Collectors.groupingBy(p -> p.getSector().getName(),
                                        Collectors.counting()))
                );
    }

    private Mono<PhoneNumber> prefixOf(PhoneNumber phoneNumber) {
        return prefixesService.findByPhoneNumber(phoneNumber.getNumber())
                              .filter(Objects::nonNull)
                              .map(phoneNumber::withPrefix);
    }
}
