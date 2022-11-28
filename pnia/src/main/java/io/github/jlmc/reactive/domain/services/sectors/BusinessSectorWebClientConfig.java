package io.github.jlmc.reactive.domain.services.sectors;


import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.Connection;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class BusinessSectorWebClientConfig {

    private static final String BASE_URI = "https://challenge-business-sector-api.meza.talkdeskstg.com";

    @Bean
    @Qualifier("X")
    //@Primary
    //@Qualifier("businessSectorWebClient")
    public WebClient businessSectorWebClient() {

        final long timeout = 4L;

        HttpClient client = HttpClient.create()
                                      .responseTimeout(Duration.ofSeconds(timeout))
                                      .doOnConnected((Connection connection) ->
                                              connection.addHandlerLast(new ReadTimeoutHandler(timeout, TimeUnit.SECONDS))
                                                        .addHandlerLast(new WriteTimeoutHandler(timeout, TimeUnit.SECONDS)))
                ;

        return WebClient.builder()
                        .baseUrl(BASE_URI)
                        .clientConnector(new ReactorClientHttpConnector(client))
                        .build();
    }
}
