package io.github.jlmc.poc.configurations.openfeign;

import feign.Retryer;
import feign.codec.ErrorDecoder;
import io.github.jlmc.poc.OpenfeignResilientCustomisationsApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = OpenfeignResilientCustomisationsApplication.class)
public class OpenFeignConfiguration {

    @Bean
    public Retryer retryer() {
        return new FixedDelayRetryer();
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
