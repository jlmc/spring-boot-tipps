package io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.billing;

import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.BillingEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

import static io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.KafkaConfiguration.BILLING;

@Configuration
public class BillingKafkaProducerConfiguration {

    @Bean
    public ProducerFactory<String, BillingEvent> producerBillingFactory() {
        Map<String, Object> config = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BILLING,
                JsonSerializer.ADD_TYPE_INFO_HEADERS, false,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, BillingEvent> kafkaBillingTemplate() {
        KafkaTemplate<String, BillingEvent> stringBillingEventKafkaTemplate = new KafkaTemplate<>(producerBillingFactory());



        return stringBillingEventKafkaTemplate;
    }

}
