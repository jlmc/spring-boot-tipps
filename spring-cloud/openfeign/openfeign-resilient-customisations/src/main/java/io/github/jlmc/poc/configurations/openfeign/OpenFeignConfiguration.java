package io.github.jlmc.poc.configurations.openfeign;

import io.github.jlmc.poc.OpenfeignResilientCustomisationsApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackageClasses = OpenfeignResilientCustomisationsApplication.class)
public class OpenFeignConfiguration {
}
