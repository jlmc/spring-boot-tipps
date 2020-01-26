package io.costax.food4u.core.openapi;

import com.fasterxml.classmate.TypeResolver;
import io.costax.food4u.api.exceptionhandler.Problem;
import io.costax.food4u.api.model.requests.output.RequestSummaryOutputRepresentation;
import io.costax.food4u.api.openapi.models.PageableModelOpenApi;
import io.costax.food4u.api.openapi.models.RequestPageOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandler;

import static io.costax.food4u.core.openapi.GlobalHttpVerbResponseMessages.*;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class) // integrate the beanValidations annotations with OpenAPi
public class SpringFoxOpenApiConfiguration implements WebMvcConfigurer {

    @Bean
    public Docket apiDocket() {

        TypeResolver typeResolver = new TypeResolver();

        // to generation of
        // docket is the SpringFox Document of the documentation
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                //.apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("io.costax.food4u.api"))
                .build()
                // do not use the default status code strategy documentation
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, getGetHttpVerbResponseMessages())
                .globalResponseMessage(RequestMethod.POST, getPostHttpVerbResponseMessages())
                .globalResponseMessage(RequestMethod.PUT, getPutHttpVerbResponseMessages())
                .globalResponseMessage(RequestMethod.DELETE, getDeleteHttpVerbResponseMessages())
                // do not add to the open Api the parameter of the types
                .ignoredParameterTypes(
                        ServletWebRequest.class,
                        URL.class,
                        URI.class,
                        URLStreamHandler.class,
                        Resource.class,
                        File.class,
                        InputStreamResource.class,
                        InputStream.class)
                // adding additional model that is missing in the documentation
                .additionalModels(typeResolver.arrayType(Problem.class))
                // Use a substitute class in the documentation
                .directModelSubstitute(Pageable.class, PageableModelOpenApi.class)
                .alternateTypeRules(AlternateTypeRules.newRule(
                        typeResolver.resolve(Page.class, RequestSummaryOutputRepresentation.class),
                        RequestPageOpenApi.class))
                // configure the title and description of the documentation
                .apiInfo(apiInfo())
                // Api documentations tags, basally are the titles that group endpoints requests
                .tags(
                        new Tag("Cookers", "Management of cookers"),
                        new Tag("Groups", "Management of user groups"),
                        new Tag("Payment methods", "Management Payment Methods"),
                        new Tag("Restaurants", "Restaurant Management"),
                        new Tag("Requests", "Requests Management"),
                        new Tag("Statistics", "Get Statistics Reports")
                );

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
