package io.github.jlmc.uploadcsv.configurations.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

//@EnableWebFlux // https://stackoverflow.com/a/65104782
@Configuration
public class CorsGlobalConfiguration implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                    .allowedOrigins("https://allowed-origin.com")
                    .allowedMethods(
                            HttpMethod.GET.name(),
                            HttpMethod.POST.name(),
                            HttpMethod.PUT.name(),
                            HttpMethod.DELETE.name(),
                            HttpMethod.PATCH.name(),
                            HttpMethod.OPTIONS.name(),
                            HttpMethod.HEAD.name()
                    )
                    .allowedOriginPatterns("*")
                    .allowCredentials(true)
                    .allowedHeaders("Accept", "Content-Type")
                    .maxAge(3600);
    }
}
