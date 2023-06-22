package io.github.jlmc.uploadcsv.adapters.in.rest.errors;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data

@Validated
@Configuration
@ConfigurationProperties(prefix = "app.api.errors")
public class ApiErrorsConfigurationProperties {

    @NotBlank
    private String code = "XXXX";
}
