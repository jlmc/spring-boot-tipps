package io.github.jlmc.poc.adapters.products.control;

import feign.Logger;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditorLogger;
import io.github.jlmc.poc.configurations.openfeign.logger.Auditor;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

//@Configuration
// ProductsServiceClientConfiguration does not need to be annotated with @Configuration. However, if it is, then take care to exclude it from any @ComponentScan that would otherwise include this configuration as it will become the default source for feign.Decoder, feign.Encoder, feign.Contract, etc., when specified. This can be avoided by putting it in a separate, non-overlapping package from any @ComponentScan or @SpringBootApplication, or it can be explicitly excluded in @ComponentScan.
public class ProductsServiceClientConfiguration {

    @Bean
    public Logger auditFeignLogger(Auditor auditor, Clock clock) {
        return new AuditorLogger(auditor, clock);
    }

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("X-POP-USER", "X-POP-PASSWORD");
    }

}
