package io.costax.food4u.core.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * The web configuration class enables the CORS in all the request
 *
 * Here in this class we can add Servlet filters to the applications
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

    @Bean
    public Filter shallowETagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }
}
