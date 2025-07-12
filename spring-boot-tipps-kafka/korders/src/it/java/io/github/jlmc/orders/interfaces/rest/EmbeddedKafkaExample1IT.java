package io.github.jlmc.orders.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@DirtiesContext // Ensures context is cleaned after test
@Import(EmbeddedKafkaExample1IT.MyKafkaConsumer.class)
@EmbeddedKafka(partitions = 1, topics = {"test-topic"}, brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
})
public class EmbeddedKafkaExample1IT {

    static CountDownLatch latch = new CountDownLatch(1);


    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
    }

    @Test
    void testSendMessage() throws InterruptedException {
        kafkaTemplate.send("test-topic", "key", "hello embedded kafka");

        boolean messageReceived = latch.await(10, TimeUnit.SECONDS);
        assert messageReceived;
    }

    @Component
    static class MyKafkaConsumer {

        MyKafkaConsumer() {
            System.out.println("MyKafkaConsumer constructor");
        }

        @KafkaListener(topics = "test-topic", groupId = "test-group")
        public void listen(String message) {
            System.out.println("Received message: " + message);
            // You can store the message or call a handler for assertions in tests
            latch.countDown();
        }

    }
}
