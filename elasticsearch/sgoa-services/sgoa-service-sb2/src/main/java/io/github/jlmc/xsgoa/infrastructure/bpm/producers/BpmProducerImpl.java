package io.github.jlmc.xsgoa.infrastructure.bpm.producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class BpmProducerImpl {

    @Autowired
    BpmProducer bpmProducer;

    @Value("${config.messaging.routingExpression}")
    private String routingKeyExpression;

    @Value("${spring.application.name}")
    private String appName;

    public void startProcess(final String startProcessDto) {

        Message<String> build = MessageBuilder
                .withPayload(startProcessDto)
                .build();

        bpmProducer.startProcess().send(build);
    }

    public void correlateMessage(final String correlateMessageDto) {

        Message<String> build = MessageBuilder
                .withPayload(correlateMessageDto)
                .setHeader(routingKeyExpression, appName)
                .build();

        bpmProducer.correlateMessage().send(build);
    }

    public void subscribeTopics(String subscribeWorkerDto) {
        Message<String> message = MessageBuilder.withPayload(subscribeWorkerDto).build();
        bpmProducer.subscribeTopics().send(message);
    }

    public void deployBpm(final String deployDto) {

        Message<String> build = MessageBuilder
                .withPayload(deployDto)
                .build();

        bpmProducer.deployBpm().send(build);
    }
}
