package io.costax.versioningapis.uris.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    ApiDeprecationHandler apiDeprecationHandler;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*");
                //.allowedOrigins("*")
                //.maxAge(30);
    }

    /**
     * Define the default version of the API.
     *
     * Specify the version of the api that should be used to consume the requests that not contain the `Accept` Header.
     *
     * Normally, we will always choose the highest version, because we want every consumer to migrate to the latest version..
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(ApiExampleMediaTypes.V2_APPLICATION_JSON);
    }


    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // if api v1 steel valid but deprecated
        registry.addInterceptor(apiDeprecationHandler);
    }
}
