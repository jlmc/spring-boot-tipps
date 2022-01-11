package io.github.jlmc.aop.core.audit.internal;

import lombok.Data;

@Data
public class AuditInfo {
    private final String operation;
    private final Integer severity;
    private final Object idValue;
    private Integer httpResponseStatus;
    private Throwable problem;
}
