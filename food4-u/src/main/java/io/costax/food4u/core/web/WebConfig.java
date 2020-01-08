package io.costax.food4u.core.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The web configuration class enables the CORS in all the request
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        // any request for the path mapping
        registry.addMapping("/**")
            //.allowedOrigins("http://foo.com")
            .allowedOrigins("*")
            //.allowedMethods("GET", "POST", "HEAD")
            .allowedMethods("*")
            .maxAge(1800)
            .allowCredentials(true)
        ;
    }
}
