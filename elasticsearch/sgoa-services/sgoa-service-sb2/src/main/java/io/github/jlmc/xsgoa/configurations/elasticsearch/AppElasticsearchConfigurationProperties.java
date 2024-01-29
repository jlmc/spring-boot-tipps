package io.github.jlmc.xsgoa.configurations.elasticsearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.elasticsearch")
public class AppElasticsearchConfigurationProperties {
    private String hostAndPort = "localhost:9200";
    private String pathPrefix;
    private boolean secured = false;
    private boolean reverseProxy = false;
    private boolean https = false;
    private String username;
    private String password;
    private String customersIndex = "customers";
}