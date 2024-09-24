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
    public String request(AuditRequestLog auditRequestLog) {
        ExternalRequestAudit entity = new ExternalRequestAudit(auditRequestLog);

        try {
            repository.saveAndFlush(entity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "" + entity.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void response(String id, AuditResponseLog auditResponseLog) {
        if (id == null) {
            throw new IllegalArgumentException("id cannot be null");
        }

        repository.findById(Long.parseLong(id))
                .ifPresent(it -> it.withResponse(auditResponseLog));
    }
}
