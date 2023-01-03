package io.github.jlmc.korders.processor.infrastruture.repositories;

import io.github.jlmc.korders.processor.domain.model.aggregates.FailureRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailureRecordRepository extends JpaRepository<FailureRecord, Integer> {

    List<FailureRecord> findAllByStatus(FailureRecord.Status status);
}
