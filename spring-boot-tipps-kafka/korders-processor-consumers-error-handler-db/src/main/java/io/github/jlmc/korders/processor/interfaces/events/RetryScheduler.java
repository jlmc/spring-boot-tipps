package io.github.jlmc.korders.processor.interfaces.events;

import io.github.jlmc.korders.processor.application.commandservices.RegisterNewOrderCommandService;
import io.github.jlmc.korders.processor.domain.model.aggregates.FailureRecord;
import io.github.jlmc.korders.processor.domain.model.commands.RegisterNewOrderCommand;
import io.github.jlmc.korders.processor.infrastruture.repositories.FailureRecordRepository;
import io.github.jlmc.korders.processor.interfaces.events.transform.OrderEventCommandAssembler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

@Service
public class RetryScheduler {
    private final FailureRecordRepository failureRecordRepository;
    private final RegisterNewOrderCommandService registerNewOrderCommandService;
    private final OrderEventCommandAssembler orderEventCommandAssembler;

    @Lazy
    @Autowired
    RetryScheduler self;

    public RetryScheduler(FailureRecordRepository failureRecordRepository, RegisterNewOrderCommandService registerNewOrderCommandService, OrderEventCommandAssembler orderEventCommandAssembler) {
        this.failureRecordRepository = failureRecordRepository;
        this.registerNewOrderCommandService = registerNewOrderCommandService;
        this.orderEventCommandAssembler = orderEventCommandAssembler;
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.MINUTES, initialDelay = 10)
    public void retryFailedRecords() {
                Flux.fromIterable(failureRecordRepository.findAllByStatus(FailureRecord.Status.TO_RETRY))
                    .map(failureRecord -> orderEventCommandAssembler.toCommand(this.buildConsumerRecord(failureRecord)))
                    .subscribeOn(Schedulers.fromExecutor())
                    .subscribe(this::accept);
    }

    @Transactional
    public void retry(RegisterNewOrderCommand command) {
        try {
            this.registerNewOrderCommandService.execute(command);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ConsumerRecord<String, String> buildConsumerRecord(FailureRecord failureRecord) {

        return new ConsumerRecord<>(
                failureRecord.getFromTopic(),
                failureRecord.getTopicPartition(),
                failureRecord.getTopicPartition(),
                failureRecord.getRecordKey(),
                failureRecord.getErrorRecord()
        );

    }

    private void accept(RegisterNewOrderCommand command) {
        self.retry(command);
    }
}
