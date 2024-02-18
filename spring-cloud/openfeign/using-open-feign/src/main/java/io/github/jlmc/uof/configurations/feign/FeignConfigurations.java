package io.github.jlmc.uof.configurations.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        basePackages = "io.github.jlmc.uof"
)
public class FeignConfigurations {

    @Bean
    UserFeignClientInterceptor userFeignClientInterceptor(MarketplaceConfigurationProperties marketplaceConfigurationProperties) {
        return new UserFeignClientInterceptor(marketplaceConfigurationProperties);
    }

    @Bean
    CustomErrorDecoder customErrorDecoder(ObjectMapper objectMapper) {
        return new CustomErrorDecoder(objectMapper);
    }

}
