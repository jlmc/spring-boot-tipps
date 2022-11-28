package io.github.jlmc.pniakotlin.domain.interactors

import io.github.jlmc.pniakotlin.domain.model.PhoneNumber
import io.github.jlmc.pniakotlin.domain.services.blacklist.BlackListService
import io.github.jlmc.pniakotlin.domain.services.prefixes.PrefixesService
import io.github.jlmc.pniakotlin.domain.services.sectors.BusinessSectorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.stream.Collectors

@Service
class GetPhoneNumberAggregation(
    private val blackListService: BlackListService,
    private val prefixesService: PrefixesService,
    private val businessSectorService: BusinessSectorService,
) {

    fun execute(phoneNumbers: List<String?>): Mono<Map<String, Map<String, Long>>> {
        blackListService.validate(phoneNumbers)

        val flux: Mono<Map<String, Map<String, Long>>> =
            Flux.fromIterable(phoneNumbers)
                .flatMap { it?.let { Mono.just(it) }?: Mono.empty() }
                .filter(PhoneNumber.Companion::isValidPhoneNumber)
                .flatMap { phoneNumberOf(it) }
                .flatMap { fetchPrefix(it) }
                .flatMap { fetchSector(it) }
                .collect(
                    Collectors.groupingBy(
                        PhoneNumber::prefix,
                        Collectors.groupingBy(
                            { it.sector!!.name },
                            Collectors.counting()
                        )
                    )
                )

        return flux
    }

    private fun phoneNumberOf(txt: String?) = txt?.let {
        Mono.fromCallable { PhoneNumber.of(it) }
    } ?: Mono.empty()

    private fun fetchPrefix(phoneNumber: PhoneNumber): Mono<PhoneNumber> {
        return prefixesService.findByPhoneNumber(phoneNumber.number)
            .filter { it != null }
            .map { phoneNumber.withPrefix(it) }
    }

    private fun fetchSector(phoneNumber: PhoneNumber): Mono<PhoneNumber> {
        return businessSectorService.getNumberSector(phoneNumber)
            .filter { it != null }
            .map { phoneNumber.withSector(it) }
    }

}
