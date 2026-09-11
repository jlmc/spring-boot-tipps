package io.github.jlmc.maydayhlsprovider.configuration;

import io.github.jlmc.maydayhlsprovider.configuration.properties.AccountsProperties;
import io.github.jlmc.maydayhlsprovider.configuration.properties.ProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ProviderProperties.class, AccountsProperties.class})
public class ConfigurationPropertiesEnablement {
}
