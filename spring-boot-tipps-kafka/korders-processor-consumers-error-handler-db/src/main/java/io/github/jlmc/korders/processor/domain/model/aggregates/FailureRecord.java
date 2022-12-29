package io.github.jlmc.korders.processor.domain.model.aggregates;

import jakarta.persistence.Convert;
import jakarta.persistence.Converts;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;


@Entity
@Getter
public class FailureRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String fromTopic;
    private String recordKey;
    private String errorRecord;
    private Integer topicPartition;
    private Long topicOffsetValue;

    @Embedded
    @Converts({
            @Convert(attributeName = "message", converter = FailureRecordCauseConverter.class),
    })
    private FailureRecordCause exceptionCause;


    public FailureRecord() {
    }


    private FailureRecord(String fromTopic, String recordKey, String errorRecord, Integer topicPartition, Long topicOffsetValue) {
        this.fromTopic = fromTopic;
        this.recordKey = recordKey;
        this.errorRecord = errorRecord;
        this.topicPartition = topicPartition;
        this.topicOffsetValue = topicOffsetValue;
    }

    public static FailureRecord createFailureRecord(String fromTopic,
                                                    String recordKey,
                                                    String errorRecord,
                                                    Integer topicPartition,
                                                    Long topicOffsetValue,
                                                    Throwable caused) {
        FailureRecord failureRecord = new FailureRecord(fromTopic, recordKey, errorRecord, topicPartition, topicOffsetValue);

        failureRecord.exceptionCause = new FailureRecordCause(
                caused.getClass().getName(),
                caused.getMessage()
        );

        return failureRecord;
    }

    @Override
    public String toString() {
        return "FailureRecord{" +
                "fromTopic='" + fromTopic + '\'' +
                ", recordKey='" + recordKey + '\'' +
                ", errorRecord='" + errorRecord + '\'' +
                '}';
    }
}
