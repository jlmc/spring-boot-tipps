package io.github.jlmc.pizzacondo.om.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.om.service.application.port.output.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KafkaOrderPublisher implements NotificationService {

    private final StreamBridge streamBridge;


}
