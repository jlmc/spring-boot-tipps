package io.github.jlmc.aop.core.audit.internal;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditNotifier.class);

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter lightOrderCounter;


    @PostConstruct
    void initMetrics() {
        lightOrderCounter = this.meterRegistry.counter("beer.orders", "OK", "ERROR"); // 1 - create a counter
       /*
        aleOrderCounter = Counter.builder("beer.orders")    // 2 - create a counter using the fluent API
                                 .tag("type", "ale")
                                 .description("The number of orders ever placed for Ale beers")
                                 .register(meterRegistry);
        */
    }

    public void register(AuditInfo auditInfo) {
        LOGGER.info("AuditNotifier: {} ", auditInfo);

        // curl http://localhost:8080/actuator/prometheus | grep beer
        lightOrderCounter.increment();
    }
}
