package io.github.jlmc.xsgoa.configurations.elasticsearch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableElasticsearchRepositories(basePackages = "io.github.jlmc.xsgoa.domain.repositories")
public class AppElasticsearchClientConfiguration {

    private final AppElasticsearchConfigurationProperties properties;

    public AppElasticsearchClientConfiguration(AppElasticsearchConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public RestClients.ElasticsearchRestClient client() {
        ClientConfiguration.MaybeSecureClientConfigurationBuilder builder = ClientConfiguration.builder().connectedTo(properties.getHostAndPort());

        if (properties.isSecured()) {
            builder.withBasicAuth(properties.getUsername(), properties.getPassword());
        }
        if (properties.isReverseProxy()) {
            builder.withPathPrefix(properties.getPathPrefix());
        }
        if (properties.isHttps()) {
            builder.usingSsl(sslContext());
        }

        return RestClients.create(builder.build());
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate(RestClients.ElasticsearchRestClient client) {
        return new ElasticsearchRestTemplate(client.rest());
    }

    private SSLContext sslContext() {
        try {
            return SSLContext.getDefault();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
