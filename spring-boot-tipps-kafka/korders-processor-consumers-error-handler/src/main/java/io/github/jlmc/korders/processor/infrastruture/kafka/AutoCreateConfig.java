package io.github.jlmc.korders.processor.infrastruture.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class AutoCreateConfig {

    @Bean
    public NewTopic ordersEvents() {
        return TopicBuilder.name("orders-events")
                           .partitions(3)
                           .replicas(1) // number clusters
                           .build();
    }

    @Bean
    public NewTopic ordersEventsRetry() {
        return TopicBuilder.name("orders-events.RETRY")
                           .partitions(3)
                           .replicas(1) // number clusters
                           .build();
    }

    @Bean
    public NewTopic ordersEventsDTL() {
        return TopicBuilder.name("orders-events.DTL")
                           .partitions(3)
                           .replicas(1) // number clusters
                           .build();
    }
}
