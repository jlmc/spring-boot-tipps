package io.github.jlmc.uof.configurations.feign;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.validation.annotation.Validated;


@Validated
@ConfigurationProperties(prefix = "app.marketplace")
public record MarketplaceConfigurationProperties(@NotBlank String authenticationToken) {
    @ConstructorBinding
    public MarketplaceConfigurationProperties {
    }
}




