package io.github.jlmc.poc.adapters.auditing.control;

import io.github.jlmc.poc.adapters.auditing.entity.ExternalRequestAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalRequestAuditRepository extends JpaRepository<ExternalRequestAudit, Long> {
}
