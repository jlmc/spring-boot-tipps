package io.github.jlmc.poc.configurations.openfeign.logger;

import java.util.Collection;
import java.util.Map;

public record AuditRequestLog(
     String url,
     String httpMethod,
     String requestBody,
     Map<String, Collection<String>> requestHeaders
) {
}
