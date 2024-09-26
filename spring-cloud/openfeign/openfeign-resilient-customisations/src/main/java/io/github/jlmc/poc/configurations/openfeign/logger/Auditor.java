package io.github.jlmc.poc.configurations.openfeign.logger;

public interface Auditor {
    void audit(AuditRequestLog auditRequestLog, AuditResponseLog auditResponseLog);
}
