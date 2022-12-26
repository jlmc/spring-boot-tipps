package io.github.jlmc.korders.processor.infrastruture.kafka;

import io.github.jlmc.korders.processor.domain.model.exceptions.IllegalProductException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;

import java.util.function.BiFunction;

public class DeadLetterPublishingRecovererDestinantionResolver implements BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterPublishingRecovererDestinantionResolver.class);

    private final String retryTopic;
    private final String dtlTopic;

    public DeadLetterPublishingRecovererDestinantionResolver(String retryTopic, String dtlTopic) {
        this.retryTopic = retryTopic;
        this.dtlTopic = dtlTopic;
    }

    @Override
    public TopicPartition apply(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        LOGGER.info("Recovered Destination Resolver: <{}>", e.getMessage());

        Throwable mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(e);

        if (mostSpecificCause instanceof IllegalProductException illegalProductException) {
            //return new TopicPartition(consumerRecord.topic() + ".failures", consumerRecord.partition());
            LOGGER.info("To the Retry: <{}> <{}>", e.getMessage(), consumerRecord);
            return new TopicPartition(retryTopic, consumerRecord.partition());
        } else {
            LOGGER.info("To the DLT: <{}> <{}>", e.getMessage(), consumerRecord);
            //return new TopicPartition(consumerRecord.topic() + ".others.failures", consumerRecord.partition());
            return new TopicPartition(dtlTopic, consumerRecord.partition());
        }
    }
}
