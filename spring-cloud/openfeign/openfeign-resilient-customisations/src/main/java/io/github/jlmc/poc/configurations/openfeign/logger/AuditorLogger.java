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

    public static final String X_AUDIT_ID = "x-audit-id";
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

        String url = request.url();
        String httpMethod = request.httpMethod().name();

        String requestBody = Optional.ofNullable(request.body()).map(String::new).orElse(null);

        Map<String, Collection<String>> requestHeaders = Map.copyOf(request.headers());


        AuditRequestLog auditRequestLog = new AuditRequestLog(url, httpMethod, requestBody, requestHeaders, Instant.now(clock));

        String id = auditor.request(auditRequestLog);// Save initial request info

        request.header(X_AUDIT_ID, id);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
        var resp =  super.logAndRebufferResponse(configKey, logLevel, response, elapsedTime);

        byte[] bodyData = null;
        Response.Body body = resp.body();
        if (body != null) {
            bodyData = Util.toByteArray(body.asInputStream());
        }

        Map<String, Collection<String>> responseHeaders = Map.copyOf(resp.headers());

        String xAuditId = resp.request().headers().get(X_AUDIT_ID).stream().findFirst().orElse(null);
        AuditResponseLog auditResponseLog = new AuditResponseLog(
                response.status(),
                Optional.ofNullable(bodyData).map(String::new).orElse(null),
                Instant.now(clock),
                responseHeaders,
                elapsedTime
        );

        auditor.response(xAuditId, auditResponseLog);

        return resp.toBuilder().body(bodyData).build();
    }

}
