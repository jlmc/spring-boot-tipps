package io.github.jlmc.pizzacondo.processing.service.application.services;

import io.github.jlmc.pizzacondo.processing.service.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j

@Service
public class PreparingService {

    private ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    @Autowired
    Worker worker;

    public void prepare(Order order) {
        log.info("schedule Preparing order {}", order);

        ses.schedule(() -> worker.doIt(order), ThreadLocalRandom.current().nextLong(10, 30), TimeUnit.SECONDS);
    }


}
