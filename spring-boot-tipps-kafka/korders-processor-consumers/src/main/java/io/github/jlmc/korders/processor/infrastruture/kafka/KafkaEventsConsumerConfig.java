package io.github.jlmc.korders.processor.infrastruture.kafka;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

/**
 * This class is required in order to enable the kafka consumer.
 * It is necessary to use @EnableKafka
 */
@Configuration
@EnableKafka
//@EnableKafkaStreams
public class KafkaEventsConsumerConfig {


    /**
     * Alternative to the default kafkaListenerContainerFactory.
     * This configurations be enabled only when we want to use
     * commit the offset MANUAL
     *
     * @see org.springframework.boot.autoconfigure.kafka.KafkaAnnotationDrivenConfiguration#kafkaListenerContainerFactory
     * @see org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
     */
    /*
    @ConditionalOnProperty(
            prefix = "xxy",
            name = "commit.offset",
            havingValue = "MANUAL",
            matchIfMissing = false
    )
    @org.springframework.context.annotation.Bean("kafkaListenerContainerFactoryManualAckMode")
    */
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactoryManualAckMode(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                defaultConcurrentKafkaListenerContainerFactory(configurer, kafkaConsumerFactory, properties);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    /**
     * This kafkaListenerContainerFactoryConcurrentConsumerThreads Bean
     * is an example how you can create concurrent instances of the message listener
     */
    @org.springframework.context.annotation.Bean("kafkaListenerContainerFactoryConcurrentConsumerThreads")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactoryConcurrentConsumerThreads(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory,
            KafkaProperties properties
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                defaultConcurrentKafkaListenerContainerFactory(configurer, kafkaConsumerFactory, properties);

        factory.setConcurrency(3);

        return factory;
    }

    private static ConcurrentKafkaListenerContainerFactory<Object, Object> defaultConcurrentKafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer, ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory, KafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, kafkaConsumerFactory
                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties())));
        return factory;
    }
}
