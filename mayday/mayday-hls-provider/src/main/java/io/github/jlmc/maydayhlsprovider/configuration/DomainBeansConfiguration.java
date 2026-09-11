package io.github.jlmc.maydayhlsprovider.configuration;

import io.github.jlmc.maydayhlsprovider.adapters.out.provider.WebClientProviderEncounterSearchAdapter;
import io.github.jlmc.maydayhlsprovider.application.SearchEncountersService;
import io.github.jlmc.maydayhlsprovider.application.port.in.SearchEncountersUseCase;
import io.github.jlmc.maydayhlsprovider.application.port.out.AccountConcurrencyPolicy;
import io.github.jlmc.maydayhlsprovider.application.port.out.ProviderEncounterSearchPort;
import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties;
import io.github.jlmc.maydayhlsprovider.domain.FixedSizeTimeWindowSplitter;
import io.github.jlmc.maydayhlsprovider.domain.TimeWindowSplitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Wires the Application Core (which has zero Spring annotations of its own) into the
 * Spring context. Kept separate from the domain/application packages on purpose, so
 * those packages stay free of framework imports.
 */
@Configuration
public class DomainBeansConfiguration {

    @Bean
    public TimeWindowSplitter timeWindowSplitter() {
        return new FixedSizeTimeWindowSplitter();
    }

    @Bean
    public ProviderEncounterSearchPort providerEncounterSearchPort(WebClient providerWebClient,
                                                                     ProviderProperties providerProperties) {
        return new WebClientProviderEncounterSearchAdapter(providerWebClient, providerProperties.retry());
    }

    @Bean
    public SearchEncountersUseCase searchEncountersUseCase(TimeWindowSplitter timeWindowSplitter,
                                                             ProviderEncounterSearchPort providerEncounterSearchPort,
                                                             AccountConcurrencyPolicy accountConcurrencyPolicy,
                                                             ProviderProperties providerProperties) {
        return new SearchEncountersService(timeWindowSplitter, providerEncounterSearchPort, accountConcurrencyPolicy,
                providerProperties.search().windowSize());
    }
}
