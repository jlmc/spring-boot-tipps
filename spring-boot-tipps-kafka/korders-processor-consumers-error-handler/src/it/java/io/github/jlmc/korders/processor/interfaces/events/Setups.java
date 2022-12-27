package io.github.jlmc.korders.processor.interfaces.events;

import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.Collection;
import java.util.Objects;

public class Setups {

    static void startupListenerContainers(KafkaListenerEndpointRegistry endpointRegistry, EmbeddedKafkaBroker embeddedKafkaBroker) {
        Collection<MessageListenerContainer> listenerContainers = endpointRegistry.getListenerContainers();
        if (listenerContainers.size() == 1) {
            for (MessageListenerContainer listenerContainer : listenerContainers) {
                ContainerTestUtils.waitForAssignment(listenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
            }
        } else {
            listenerContainers.stream()
                              .filter(messageListenerContainer ->
                                      Objects.equals(messageListenerContainer.getGroupId(), "order-events-listener-group")
                              )
                              .limit(1L)
                              .forEach(messageListenerContainer -> {
                                  ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafkaBroker.getPartitionsPerTopic());
                              });
        }
    }

}
