package io.github.jlmc.poc.configurations.openfeign.logger;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class AuditorLogger extends Logger {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AuditorLogger.class);

    private final Auditor auditor;
    private final Clock clock;

    public AuditorLogger(Auditor auditor, Clock clock) {
        super();
        this.auditor = auditor;
        this.clock = clock;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (LOGGER.isDebugEnabled()) {
            //noinspection StringConcatenationArgumentToLogCall
            LOGGER.debug(String.format(methodTag(configKey) + format, args));
        }
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        super.logRequest(configKey, logLevel, request);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        Instant now = Instant.now(clock);

        var resp =  super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);

        Instant requestInstant = now.minusMillis(elapsedTime);

        Request request = resp.request();
        String url = request.url();
        String httpMethod = request.httpMethod().name();
        String requestBody = asString(request.body());
        Map<String, Collection<String>> requestHeaders = Map.copyOf(request.headers());
        AuditRequestLog auditRequestLog = new AuditRequestLog(url, httpMethod, requestBody, requestHeaders, requestInstant);

        byte[] responseBodyData = responseBodyDataOrNull(resp);
        Map<String, Collection<String>> responseHeaders = Map.copyOf(resp.headers());

        AuditResponseLog auditResponseLog = new AuditResponseLog(
                response.status(),
                asString(responseBodyData),
                now,
                responseHeaders,
                elapsedTime
        );

        auditor.audit(auditRequestLog, auditResponseLog);

        return resp.toBuilder().body(responseBodyData).build();
    }

    private static String asString(byte[] responseBodyData) {
        return Optional.ofNullable(responseBodyData).map(String::new).orElse(null);
    }

    private byte[] responseBodyDataOrNull(Response resp) throws IOException {
        byte[] responseBodyData = null;
        Response.Body body = resp.body();
        if (body != null) {
            responseBodyData = Util.toByteArray(body.asInputStream());
        }
        return responseBodyData;
    }
}
