package io.github.jlmc.pniakotlin.domain.services.sectors

import io.github.jlmc.pniakotlin.domain.services.sectors.trace.ExchangeFilterTracer
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class BusinessSectorWebClientConfig {

    companion object {
        private const val BASE_URI = "https://challenge-business-sector-api.meza.talkdeskstg.com"
        private const val TIMEOUT_IN_SECONDS = 4L
    }

    @Bean
    @Qualifier("X")
    fun businessSectorWebClient(): WebClient {
        val client = httpClient()

        return WebClient.builder()
            .baseUrl(BASE_URI)
            .filter(ExchangeFilterTracer.logRequest())
            .filter(ExchangeFilterTracer.logResponse())
            .clientConnector(ReactorClientHttpConnector(client))
            .build()
    }

    private fun httpClient() =
        HttpClient.create()
            .responseTimeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS))
            .doOnConnected { connection: Connection ->
                connection
                    .addHandlerLast(ReadTimeoutHandler(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS))
                    .addHandlerLast(WriteTimeoutHandler(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS))
            }

}
