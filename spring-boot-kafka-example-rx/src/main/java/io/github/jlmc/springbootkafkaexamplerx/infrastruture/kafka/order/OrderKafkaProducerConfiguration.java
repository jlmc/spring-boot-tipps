package io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.order;

import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.OrderBookedEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.KafkaConfiguration.ORDER;

@Configuration
public class OrderKafkaProducerConfiguration {

    @Bean
    public ProducerFactory<String, OrderBookedEvent> orderBookedEventProducerFactory() {
        Map<String, Object> configs = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ORDER,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(configs);
    }

    @Bean
    public KafkaTemplate<String, OrderBookedEvent> orderBookedEventKafkaTemplate() {
        return new KafkaTemplate<>(orderBookedEventProducerFactory());
    }

}
