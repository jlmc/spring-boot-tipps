package io.github.jlmc.uploadcsv.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;


@Configuration
public class JacksonConfiguration implements WebFluxConfigurer {

    /*
    private final ObjectMapper mapper;

    public JacksonConfiguration(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
        WebFluxConfigurer.super.configureHttpMessageCodecs(configurer);
    }

     */
}
