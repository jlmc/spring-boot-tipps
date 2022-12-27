package io.github.jlmc.korders.processor.infrastruture.kafka;

import io.github.jlmc.korders.processor.domain.model.exceptions.IllegalProductException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

public class DeadLetterPublishingRecovererDestinantionResolver implements BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeadLetterPublishingRecovererDestinantionResolver.class);

    private static final String RETRY_COUNT_HEADER_KEY = "X_RETRY_COUNT";

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

        if (mostSpecificCause instanceof IllegalProductException) {
            //return new TopicPartition(consumerRecord.topic() + ".failures", consumerRecord.partition());
            LOGGER.info("To the Retry: <{}> <{}>", e.getMessage(), consumerRecord);

            int totalOfRetries = getRetries(consumerRecord);

            if (totalOfRetries >= 3) {
                return toDLT(consumerRecord, e);
            } else {
                return toRetry(consumerRecord);
            }
        } else {
            return toDLT(consumerRecord, e);
        }
    }

    private TopicPartition toRetry(ConsumerRecord<?, ?> consumerRecord) {
        return new TopicPartition(retryTopic, consumerRecord.partition());
    }

    private TopicPartition toDLT(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        LOGGER.info("To the DLT: <{}> <{}>", e.getMessage(), consumerRecord);
        //return new TopicPartition(consumerRecord.topic() + ".others.failures", consumerRecord.partition());
        return new TopicPartition(dtlTopic, consumerRecord.partition());
    }

    @SuppressWarnings("unused")
    public Headers applyHeaders(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        int retries = getRetries(consumerRecord);

        retries++;

        ArrayList<Header> newHeaders =
                new ArrayList<>(
                        Arrays.stream(consumerRecord.headers().toArray())
                              .filter(it -> !it.key().equals(RETRY_COUNT_HEADER_KEY)).toList());

        newHeaders.add(new RecordHeader(RETRY_COUNT_HEADER_KEY, ("" + retries).getBytes(StandardCharsets.UTF_8)));

        return new RecordHeaders(newHeaders);
    }

    private int getRetries(ConsumerRecord<?, ?> consumerRecord) {
        return Arrays.stream(consumerRecord.headers().toArray())
                     .filter(it -> it.key().equals(RETRY_COUNT_HEADER_KEY))
                     .map(it -> new String(it.value()))
                     .map(Integer::parseInt)
                     .max(Integer::compareTo)
                     .orElse(-1);
    }
}
