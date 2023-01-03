package io.github.jlmc.korders.processor.infrastruture.kafka;

import io.github.jlmc.korders.processor.application.commandservices.FailedRecordWriterService;
import io.github.jlmc.korders.processor.domain.model.exceptions.IllegalProductException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;

public class DatabaseConsumerRecordRecoverer implements ConsumerRecordRecoverer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConsumerRecordRecoverer.class);

    private final FailedRecordWriterService service;

    public DatabaseConsumerRecordRecoverer(FailedRecordWriterService service) {
        this.service = service;
    }

    @Override
    public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
        LOGGER.info("Recovered Destination Resolver: <{}>", e.getMessage());

        Throwable mostSpecificCause = NestedExceptionUtils.getMostSpecificCause(e);

        if (mostSpecificCause instanceof IllegalProductException) {
            LOGGER.info("To the Retry: <{}> <{}>", e.getMessage(), consumerRecord);

            // recovery logic
            ConsumerRecord<String, String> cr = (ConsumerRecord<String, String>) consumerRecord;
            service.add(cr, mostSpecificCause, "RETRY");

        } else {
            // non recovery logic
            ConsumerRecord<String, String> cr = (ConsumerRecord<String, String>) consumerRecord;
            service.add(cr, mostSpecificCause, "DL");
        }
    }
}
