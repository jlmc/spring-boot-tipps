package io.github.jlmc.aop.core.audit.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditNotifier.class);

    public void register(AuditInfo auditInfo) {

        LOGGER.info("AuditNotifier: {} ", auditInfo);

    }
}
