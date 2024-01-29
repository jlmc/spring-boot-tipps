package io.github.jlmc.xsgoa.infrastructure.bpm.producers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class BpmProducer {

    @Value("${config.messaging.routingExpression}")
    private String routingKeyExpression;

    @Value("${spring.application.name}")
    private String appName;

    private final StreamBridge streamBridge;

    public BpmProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void startProcess(final String startProcessDto) {

        Message<String> build = MessageBuilder
                .withPayload(startProcessDto)
                .build();

        //bpmProducer.startProcess().send(build);

        streamBridge.send(BpmProducerChannelNames.START_PROCESS, build);

    }

    public void correlateMessage(final String correlateMessageDto) {

        Message<String> build = MessageBuilder
                .withPayload(correlateMessageDto)
                .setHeader(routingKeyExpression, appName)
                .build();

       // bpmProducer.correlateMessage().send(build);

        streamBridge.send(BpmProducerChannelNames.CORRELATE_MESSAGE, build);


    }

    public void subscribeTopics(String subscribeWorkerDto) {
        Message<String> message = MessageBuilder.withPayload(subscribeWorkerDto).build();
        //bpmProducer.subscribeTopics().send(message);

        streamBridge.send(BpmProducerChannelNames.SUBSCRIBE_TOPICS, message);
    }

    public void deployBpm(final String deployDto) {

        Message<String> build = MessageBuilder
                .withPayload(deployDto)
                .build();

        //bpmProducer.deployBpm().send(build);

        streamBridge.send(BpmProducerChannelNames.DEPLOY_BPM, build);
    }
}
