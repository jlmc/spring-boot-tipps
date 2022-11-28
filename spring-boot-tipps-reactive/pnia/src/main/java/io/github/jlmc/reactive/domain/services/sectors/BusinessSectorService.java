package io.github.jlmc.reactive.domain.services.sectors;

import io.github.jlmc.reactive.domain.model.PhoneNumber;
import io.github.jlmc.reactive.domain.model.Sector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.TimeoutException;


@Service
public class BusinessSectorService {

    private final WebClient businessSectorWebClient;

    public BusinessSectorService(@Autowired @Qualifier("X") WebClient businessSectorWebClient) {
        this.businessSectorWebClient = businessSectorWebClient;
    }

    public Mono<Sector> getNumberSector(PhoneNumber phoneNumber) {
        return businessSectorWebClient.get()
                                      .uri("/sector/{phoneNumber}", phoneNumber.getNumber())
                                      .accept(MediaType.APPLICATION_JSON)
                                      .retrieve()
                                      .bodyToMono(Sector.class)
                                      .retryWhen(
                                              Retry.backoff(2, Duration.ofMillis(100))
                                                   .filter(this::shouldRetry)
                                      )
                                      .onErrorResume(e -> Mono.empty())
                                      .log("Sector Of <" + phoneNumber + "> retried")
                                      .filter(it -> it.getName() != null)
                                      .switchIfEmpty(Mono.empty());
    }

    private boolean shouldRetry(Throwable throwable) {
        return (throwable instanceof WebClientResponseException t && t.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) ||
                throwable instanceof TimeoutException;
    }
}
