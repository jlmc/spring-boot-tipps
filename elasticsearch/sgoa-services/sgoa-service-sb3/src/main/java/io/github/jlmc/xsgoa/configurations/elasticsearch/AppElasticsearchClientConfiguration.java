package io.github.jlmc.xsgoa.configurations.elasticsearch;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.github.jlmc.xsgoa.domain")
public class AppElasticsearchClientConfiguration extends ElasticsearchConfiguration {

    private final AppElasticsearchConfigurationProperties properties;

    public AppElasticsearchClientConfiguration(AppElasticsearchConfigurationProperties properties) {
        this.properties = properties;
    }

    @Override
    public @NotNull ClientConfiguration clientConfiguration() {
        String hostAndPort = properties.getHostAndPort();

        var builder = ClientConfiguration.builder().connectedTo(hostAndPort);

        if (properties.isSecured()) {
            builder.withBasicAuth(properties.getUsername(), properties.getPassword());
        }
        if (properties.isReverseProxy()) {
            builder.withPathPrefix(properties.getPathPrefix());
        }
        if (properties.isHttps()) {
            builder.usingSsl(sslContext());
        }

        return builder.build();
    }

    private SSLContext sslContext() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
