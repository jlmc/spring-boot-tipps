package io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.billing;

import io.github.jlmc.springbootkafkaexamplerx.sharewddomain.BillingEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

import static io.github.jlmc.springbootkafkaexamplerx.infrastruture.kafka.KafkaConfiguration.BILLING;

@Configuration
public class BillingKafkaConsumerConfiguration {

    @Bean
    public ConsumerFactory<String, BillingEvent> BillingEventConsumerFactory() {
        // Creating a map of string-object type
        Map<String, Object> config =
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BILLING,
                        ConsumerConfig.GROUP_ID_CONFIG, "group_id",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                );


        // Returning message in JSON format
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                new JsonDeserializer<>(BillingEvent.class));
    }

    // Creating a Listener
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BillingEvent> billingListener() {
        ConcurrentKafkaListenerContainerFactory<String, BillingEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(BillingEventConsumerFactory());

        return factory;
    }
}
