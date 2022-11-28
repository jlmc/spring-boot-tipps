package io.costax.examplesapi.tennacy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenancyConfiguration {

    @Value("${app.multiTenancy.enable:false}")
    boolean multiTenancy;

    @Bean
    TenancyMigration tenancyMigration() {
        if (!multiTenancy) {
            return null;
        }

        return (s) -> System.out.printf(" ##### Executing the Migration of [%s]\n", s);
    }
}
