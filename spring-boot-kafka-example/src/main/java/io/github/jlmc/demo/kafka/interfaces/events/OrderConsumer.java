package io.github.jlmc.demo.kafka.interfaces.events;

import io.github.jlmc.demo.kafka.shareddomain.events.OrderBookedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderConsumer.class);

   // @KafkaListener//(id = "fooGroup", topics = "topic1")
    @KafkaListener(topics = "topic1",
            groupId = "group_id",
            containerFactory = "bookListener")
    public void listen(@Payload OrderBookedEvent event, @Headers MessageHeaders headers) {

        LOGGER.info("Received HEADERs: " + headers);
        LOGGER.info("Received PAYLOAD: " + event);


        //this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
    }

}
