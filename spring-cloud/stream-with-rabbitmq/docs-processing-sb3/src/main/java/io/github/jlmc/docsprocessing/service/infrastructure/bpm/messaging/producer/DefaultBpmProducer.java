package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.StartProcessDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

//@Component
//@RequiredArgsConstructor
//@Slf4j
@Component
@Slf4j
public class DefaultBpmProducer {

    public static final String START_PROCESS = "startProcess";
    public static final String CORRELATE_MESSAGE = "correlateMessage";
    public static final String SUBSCRIBE_TOPICS = "subscribeTopics";
    public static final String DEPLOY_BPM = "deployBpm";

    @Value("${config.messaging.routingExpression}")
    String routingKeyExpression;

    @Value("${spring.application.name}")
    String appName;


    private final StreamBridge publisher;

    public DefaultBpmProducer(StreamBridge publisher) {
        this.publisher = publisher;
    }

    /**
     * Start process.
     */
    public void startProcess(final String processName) {

        // Exchange: bpm.start.process [fanout, durable]
        // Queue bpm.start.process.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        StartProcessDto startProcessDto = new StartProcessDto(processName);
        Message<StartProcessDto> build = MessageBuilder
                .withPayload(startProcessDto)
                .build();

        //bpmProducer.ifPresent(it -> it.startProcess().send(build));

        publisher.send(START_PROCESS, build);


        //bpmProducer.startProcess().send(build);
        log.info(startProcessDto.processName() + " - Process started");
    }

    public void correlateMessage(final CorrelateMessageDto correlateMessageDto) {
        // Exchange: bpm.correlate.message [fanout, durable]
        // queue: bpm.correlate.message.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        Message<CorrelateMessageDto> message = MessageBuilder
                .withPayload(correlateMessageDto)
                .setHeader(routingKeyExpression, appName)
                .build();

        publisher.send(CORRELATE_MESSAGE, message);

        log.info("Message sent for correlate message {}", message);
    }


    public void subscribeTopics(SubscribeWorkerDto subscribeWorkerDto) {
        log.debug("Sending message for subscribe topics: {}", subscribeWorkerDto);

        Message<SubscribeWorkerDto> message = MessageBuilder.withPayload(subscribeWorkerDto).build();

        publisher.send(SUBSCRIBE_TOPICS, message);

        log.info("Message sent for subscribe topics");
    }


    public void deployBpm(DeployDto deployDto) {
        // Exchange: bpm.deploy.workflow [fanout, durable]
        // Queue bpm.deploy.workflow.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        Message<DeployDto> message = MessageBuilder
                .withPayload(deployDto)
                .build();

        publisher.send(DEPLOY_BPM, message);

        log.info(deployDto.getN() + " - workflow resource sent");
    }

    @Data
    public static class CorrelateMessageDto {
        String n = getClass().getName();
    }

    @Data
    public static class SubscribeWorkerDto {
        String n = getClass().getName();
    }

    @Data
    public static class DeployDto {
        String n = getClass().getName();
    }


}
