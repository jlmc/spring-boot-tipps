package io.costax.food4u.core.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxOpenApiConfiguration implements WebMvcConfigurer {

    @Bean
    public Docket apiDocket() {
        // to generation of
        // docket is the SpringFox Document of the documentation
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                //.apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("io.costax.food4u.api"))
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Customisations of the swagger-ui default titles and descriptions
     */
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Food-4-U Api")
                .description("Open API for restaurants clients")
                .version("1")
                .contact(new springfox.documentation.service.Contact("costax", "https://github.com/jlmc", "costajlmpp@gmail.com"))
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // to generation of /swagger-ui.html
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }



}
