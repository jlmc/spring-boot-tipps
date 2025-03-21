package io.github.jlmc.poc.adapters.auditing.boundary;

import io.github.jlmc.poc.adapters.auditing.control.ExternalRequestAuditRepository;
import io.github.jlmc.poc.adapters.auditing.entity.ExternalRequestAudit;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditRequestLog;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditResponseLog;
import io.github.jlmc.poc.configurations.openfeign.logger.Auditor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DatabaseExternalRequestAuditor implements Auditor {

    private final ExternalRequestAuditRepository repository;

    public DatabaseExternalRequestAuditor(ExternalRequestAuditRepository repository) {
        this.repository = repository;
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void audit(AuditRequestLog auditRequestLog, AuditResponseLog auditResponseLog) {
        try {
            ExternalRequestAudit entity = new ExternalRequestAudit(auditRequestLog).withResponse(auditResponseLog);
            repository.saveAndFlush(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
