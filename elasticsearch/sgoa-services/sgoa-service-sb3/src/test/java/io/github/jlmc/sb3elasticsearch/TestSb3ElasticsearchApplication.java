package io.github.jlmc.sb3elasticsearch;

import io.github.jlmc.xsgoa.Sb3ElasticsearchApplication;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@Disabled
@TestConfiguration(proxyBeanMethods = false)
public class TestSb3ElasticsearchApplication {

    @Bean
    @ServiceConnection
    ElasticsearchContainer elasticsearchContainer() {
        return new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.10"));
    }

    public static void main(String[] args) {
        SpringApplication.from(Sb3ElasticsearchApplication::main).with(TestSb3ElasticsearchApplication.class).run(args);
    }

}
