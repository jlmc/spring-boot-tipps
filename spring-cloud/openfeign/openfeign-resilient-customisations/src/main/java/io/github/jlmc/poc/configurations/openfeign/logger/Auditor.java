package io.github.jlmc.poc.configurations.openfeign.logger;

public interface Auditor {

    String request(AuditRequestLog auditRequestLog);

    void response(String id, AuditResponseLog auditResponseLog);
}
