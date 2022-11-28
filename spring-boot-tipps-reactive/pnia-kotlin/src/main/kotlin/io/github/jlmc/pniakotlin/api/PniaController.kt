package io.github.jlmc.pniakotlin.api

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import io.github.jlmc.pniakotlin.domain.interactors.GetPhoneNumberAggregation
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping(path = ["aggregate"])
class PniaController(private val aggregation: GetPhoneNumberAggregation) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createAggregation(
        @RequestBody @Validated phoneNumbers: List<String?>
    ): Mono<Map<String, Map<String, Long>>> {
        return aggregation.execute(phoneNumbers)
    }
}
