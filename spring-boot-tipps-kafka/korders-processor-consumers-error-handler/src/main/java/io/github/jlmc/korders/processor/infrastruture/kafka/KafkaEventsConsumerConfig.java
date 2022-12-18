package io.github.jlmc.korders.processor.infrastruture.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.RetryListener;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;

/**
 * This class is required in order to enable the kafka consumer.
 * It is necessary to use @EnableKafka
 */
@Configuration
@EnableKafka
//@EnableKafkaStreams
public class KafkaEventsConsumerConfig {

    public static final int  CUSTOM_MAX_FAILURES = 3;

    public org.springframework.kafka.listener.DefaultErrorHandler errorHandler() {
        BackOff backOff = new FixedBackOff(Duration.ofSeconds(1).toMillis(), CUSTOM_MAX_FAILURES - 1);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(backOff);

        // Add a RetryListener to monitor each Retry attempt
        errorHandler.setRetryListeners(customRetryListener());

        return errorHandler;
    }

    public RetryListener customRetryListener() {
        return new CustomRetryListener();
    }

    @org.springframework.context.annotation.Bean("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                defaultConcurrentKafkaListenerContainerFactory(configurer, kafkaConsumerFactory, properties);

        factory.setConcurrency(3);

        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }

    private static ConcurrentKafkaListenerContainerFactory<Object, Object> defaultConcurrentKafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory, KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties())));
        return factory;
    }
}
