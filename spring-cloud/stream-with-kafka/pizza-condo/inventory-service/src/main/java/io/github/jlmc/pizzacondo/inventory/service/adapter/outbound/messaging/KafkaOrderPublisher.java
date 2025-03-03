package io.github.jlmc.pizzacondo.inventory.service.adapter.outbound.messaging;

import io.github.jlmc.pizzacondo.inventory.service.application.port.output.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KafkaOrderPublisher implements NotificationService {

    private final StreamBridge streamBridge;


}
