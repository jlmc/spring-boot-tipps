package io.github.jlmc.demo.kafka.infrastructure.kafka;

import io.github.jlmc.demo.kafka.shareddomain.events.OrderBookedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static io.github.jlmc.demo.kafka.infrastructure.kafka.KafkaConfiguration.ORDER;

@Configuration
public class OrderKafkaProducerConfiguration {

    //@Value("${kafka.server.orders.address}")



    @Bean
    public ProducerFactory producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ORDER);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory(config);
    }

    @Bean
    public KafkaTemplate<String, OrderBookedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
