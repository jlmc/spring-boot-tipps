package io.github.jlmc.korders.processor.application.commandservices;

import io.github.jlmc.korders.processor.infrastruture.repositories.FailureRecordRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import static io.github.jlmc.korders.processor.domain.model.aggregates.FailureRecord.createFailureRecord;

@Service
@Transactional(readOnly = true)
public class FailedRecordWriterService {

    private final FailureRecordRepository repository;

    public FailedRecordWriterService(FailureRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, readOnly = false)
    public void add(ConsumerRecord<String, String> consumerRecord, Throwable caused, String status) {

        var failureRecord =
                createFailureRecord(
                        consumerRecord.topic(),
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.partition(),
                        consumerRecord.offset(),
                        caused
                );


        repository.saveAndFlush(failureRecord);
    }
}
