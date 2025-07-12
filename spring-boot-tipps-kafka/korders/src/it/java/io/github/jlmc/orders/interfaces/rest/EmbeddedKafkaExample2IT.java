package io.github.jlmc.orders.interfaces.rest;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@DirtiesContext // Ensures context is cleaned after test
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
})
class EmbeddedKafkaExample2IT {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @DynamicPropertySource
    static void registerKafka(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Test
    void testConsumeUsingKafkaTestUtils() {
        // Send a message
        kafkaTemplate.send("test-topic", "my-key", "my-test-message");

        // Set up test consumer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        Consumer<String, String> consumer =
                new DefaultKafkaConsumerFactory<>(
                        consumerProps,
                        new StringDeserializer(),
                        new StringDeserializer()
                ).createConsumer();
        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "test-topic");

        // Read the record
        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumer, "test-topic");

        assertThat(record).isNotNull();
        assertThat(record.value()).isEqualTo("my-test-message");
        assertThat(record.key()).isEqualTo("my-key");

        consumer.close();
    }
}