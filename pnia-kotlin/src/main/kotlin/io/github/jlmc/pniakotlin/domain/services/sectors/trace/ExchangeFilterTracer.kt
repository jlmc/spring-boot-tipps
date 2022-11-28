package io.github.jlmc.pniakotlin.domain.services.sectors.trace

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

object ExchangeFilterTracer {

    private val logger = LoggerFactory.getLogger(ExchangeFilterTracer::class.java)

    fun logRequest(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor { traceRequest(it) }
    }

    fun logResponse(): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofResponseProcessor { traceResponse(it) }
    }

    private fun traceRequest(clientRequest: ClientRequest): Mono<ClientRequest> {
        logger.info("${clientRequest.method()} ${clientRequest.url()}")
        return Mono.just(clientRequest)
    }

    private fun traceResponse(clientResponse: ClientResponse) : Mono<ClientResponse>  {
        logger.info("Response status: {}", clientResponse.statusCode())
        return Mono.just(clientResponse)
    }
}
