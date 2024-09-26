package io.github.jlmc.poc.adapters.ordersid.control.remote;

import feign.Logger;
import io.github.jlmc.poc.configurations.openfeign.logger.Auditor;
import io.github.jlmc.poc.configurations.openfeign.logger.AuditorLogger;

import java.time.Clock;

//@Configuration
public class OrderIdGeneratorServiceApiClientConfiguration {

    //@Bean
    public Logger OrderIdGeneratorServiceApiClientAuditFeignLogger(Auditor auditor, Clock clock) {
        return new AuditorLogger(auditor, clock);
    }

}
