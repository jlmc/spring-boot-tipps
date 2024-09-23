package io.github.jlmc.poc.configurations.openfeign.logger;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

public record AuditResponseLog(
    int responseStatus,
    String responseBody,
    Instant timestamp,
    Map<String, Collection<String>> responseHeaders
) {
}
