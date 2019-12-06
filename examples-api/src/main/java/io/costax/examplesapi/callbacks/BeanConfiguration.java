package io.costax.examplesapi.callbacks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean(initMethod = "init", destroyMethod = "cleanup")
    ThirdPartClass thirdPartClass() {
        return new ThirdPartClass();
    }
}
