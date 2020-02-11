package io.costax.versioningapis.uris.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    ApiDeprecationHandler apiDeprecationHandler;

    ApiRetirementHandler apiRetirementHandler;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*");
        //.allowedOrigins("*")
        //.maxAge(30);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        // if api v1 steel valid but deprecated
        registry.addInterceptor(apiDeprecationHandler);

        // if the api v1 is not valid any more
        //registry.addInterceptor(apiRetirementHandler);
    }

}
