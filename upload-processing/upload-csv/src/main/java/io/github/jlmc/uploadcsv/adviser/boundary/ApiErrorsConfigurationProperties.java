package io.github.jlmc.uploadcsv.adviser.boundary;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Data

@Validated
@Configuration
@ConfigurationProperties(prefix = "app.api.errors")
public class ApiErrorsConfigurationProperties {

    @NotBlank
    private String code = "XXXX";
}
