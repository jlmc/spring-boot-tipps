package io.costax.food4u.core.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class) // integrate the beanValidations annotations with OpenAPi
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
                // do not use the default status code strategy documentation
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, getGetResponseMessages())
                // configure the title and description of the documentation
                .apiInfo(apiInfo())
                .tags(new Tag("Cookers", "Management of cookers"));
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

    private List<ResponseMessage> getGetResponseMessages() {

        return List.of(

                new ResponseMessageBuilder()
                        .code(HttpStatus.OK.value())
                        .message("The Success response")
                        .build(),

                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Resource not found")
                        .build(),

                new ResponseMessageBuilder()
                        .code(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Resource do not contain representation for the required type")
                        .build()
        );
    }

}
