package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.producer;

import io.github.jlmc.docsprocessing.service.commons.gateway.response.StartProcessDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

//@Component
//@RequiredArgsConstructor
//@Slf4j
@Component
@Slf4j
public class DefaultBpmProducer {

    @Value("${config.messaging.routingExpression}")
    private String routingKeyExpression;

    @Value("${spring.application.name}")
    private String appName;

    private final BpmProducer bpmProducer;

    public DefaultBpmProducer(BpmProducer bpmProducer) {
        this.bpmProducer = bpmProducer;
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

        bpmProducer.startProcess().send(build);
        log.info(startProcessDto.getProcessName() + " - Process started");
    }

    public void correlateMessage(final CorrelateMessageDto correlateMessageDto) {
        // Exchange: bpm.correlate.message [fanout, durable]
        // queue: bpm.correlate.message.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        Message<CorrelateMessageDto> build = MessageBuilder
                .withPayload(correlateMessageDto)
                .setHeader(routingKeyExpression, appName)
                .build();

        bpmProducer.correlateMessage().send(build);
        log.info("Message sent for correlate message {}", build);
    }


    public void subscribeTopics(SubscribeWorkerDto subscribeWorkerDto) {
        log.debug("Sending message for subscribe topics: {}", subscribeWorkerDto);

        // Exchange: bpm.subscribe.worker [fanout, durable]
        // Queue bpm.subscribe.worker.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        Message<SubscribeWorkerDto> message = MessageBuilder.withPayload(subscribeWorkerDto).build();

        bpmProducer.subscribeTopics()
                .send(message);

        log.info("Message sent for subscribe topics");
    }


    public void deployBpm(DeployDto deployDto) {
        // Exchange: bpm.deploy.workflow [fanout, durable]
        // Queue bpm.deploy.workflow.backup [x-message-ttl:	1296000000 ,arguments: [x-queue-mode:	lazy], durable:	true ]

        Message<DeployDto> build = MessageBuilder
                .withPayload(deployDto)
                .build();

        bpmProducer.deployBpm().send(build);
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
