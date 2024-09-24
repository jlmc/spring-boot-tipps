package io.github.jlmc.poc.adapters.auditing.entity;

import io.github.jlmc.poc.configurations.openfeign.logger.AuditRequestLog;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditResponseLog;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@Entity
@Table(name = "external_request_audit")
public class ExternalRequestAudit {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "external_request_audit_seq")
    @SequenceGenerator(
            name = "external_request_audit_seq",
            allocationSize = 5
    )
    private Long id;


    private Instant requestInstant;
    private String url;
    private String httpMethod;
    //@Lob // For larger texts
    private String requestBody;



    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "request_headers")
    private Map<String, Collection<String>> requestHeaders;

    //@Lob
    private String responseBody;
    private Integer responseStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", name = "response_headers")
    private Map<String, Collection<String>> responseHeaders;
    private Instant responseInstant;
    private Long elapsedTime;

    public ExternalRequestAudit() {
    }

    public ExternalRequestAudit(AuditRequestLog auditRequestLog) {
        this.url = auditRequestLog.url();
        this.httpMethod = auditRequestLog.httpMethod();
        this.requestBody = auditRequestLog.requestBody();
        this.requestHeaders = auditRequestLog.requestHeaders();
        this.requestInstant = auditRequestLog.requestInstant();

    }

    public Long getId() {
        return id;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void withResponse(AuditResponseLog auditResponseLog) {
        this.responseStatus = auditResponseLog.responseStatus();
        this.responseBody = auditResponseLog.responseBody();
        this.responseHeaders = auditResponseLog.responseHeaders();
        this.responseInstant = auditResponseLog.responseInstant();
        this.elapsedTime = auditResponseLog.elapsedTime();
    }
}
