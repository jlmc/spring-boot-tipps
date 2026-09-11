package io.github.jlmc.maydayhlsprovider.configuration;

import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient providerWebClient(ProviderProperties properties) {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(properties.http().responseTimeout())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.http().connectTimeout().toMillis());

        return WebClient.builder()
                .baseUrl(properties.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
