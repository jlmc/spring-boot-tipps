package io.github.jlmc.xsgoa.infrastructure.bpm.producers;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface BpmProducer {
    /**
     * Start process message channel.
     *
     * @return the message channel
     */

    @Output(BpmProducerChannelNames.START_PROCESS)
    MessageChannel startProcess();


    /**
     * Correlate message message channel.
     *
     * @return the message channel
     */
    @Output(BpmProducerChannelNames.CORRELATE_MESSAGE)
    MessageChannel correlateMessage();

    /**
     * Subscribe topics message channel.
     *
     * @return the message channel
     */
    @Output(BpmProducerChannelNames.SUBSCRIBE_TOPICS)
    MessageChannel subscribeTopics();

    /**
     * Deploy workflow resource message channel.
     *
     * @return the message channel
     */
    @Output(BpmProducerChannelNames.DEPLOY_BPM)
    MessageChannel deployBpm();
}
