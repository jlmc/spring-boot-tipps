package io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

import static io.github.jlmc.docsprocessing.service.infrastructure.bpm.messaging.consumer.BpmConsumerChannelNames.*;

/**
 * The interface Bpm gateway consumer.
 */
public interface BpmConsumer {

    /**
     * Deploy bpm response subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(DEPLOY_BPM_RESPONSE)
    SubscribableChannel deployBpmResponse();

    /**
     * Receive crc validation subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(EXTERNAL_TASK_CRC_VALIDATION)
    SubscribableChannel receiveExternalTaskCrcValidation();

    /**
     * Receive external task information extraction subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(EXTERNAL_TASK_INFORMATION_EXTRACTION)
    SubscribableChannel receiveExternalTaskInformationExtraction();
}

