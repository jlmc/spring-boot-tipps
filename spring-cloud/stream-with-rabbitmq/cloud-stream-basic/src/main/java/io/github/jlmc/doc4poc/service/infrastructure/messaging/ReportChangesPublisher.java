package io.github.jlmc.doc4poc.service.infrastructure.messaging;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class ReportChangesPublisher {

    // spring.cloud.stream.bindings.reportChanges.destination=client-view-service.report.changes

    private final StreamBridge publisher;

    public ReportChangesPublisher(StreamBridge publisher) {
        this.publisher = publisher;
    }

    public void publish(OrderChanged dto) {
        publisher.send("reportChanges", MessageBuilder.withPayload(dto).build());
    }
}
