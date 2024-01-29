package io.github.jlmc.xsgoa.infrastructure.bpm.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface BpmConsumer {

    /**
     * Deploy bpm response subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(BpmConsumerChannelNames.DEPLOY_BPM_RESPONSE)
    SubscribableChannel deployBpmResponse();

    /**
     * Receive external task start sgoa process subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(BpmConsumerChannelNames.EXTERNAL_TASK_LOAD_OPERATION)
    SubscribableChannel receiveExternalTaskLoadOperation();

    /**
     * Receive external task load single operation subscribable channel.
     *
     * @return the subscribable channel
     */
    @Input(BpmConsumerChannelNames.EXTERNAL_TASK_LOAD_SINGLE_OPERATION)
    SubscribableChannel receiveExternalTaskLoadSingleOperation();
}
