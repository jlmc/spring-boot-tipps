package io.github.jlmc.pizzacondo.processing.service.application.services;

import io.github.jlmc.pizzacondo.processing.service.application.port.output.NotificationService;
import io.github.jlmc.pizzacondo.processing.service.domain.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Worker {

    @Autowired
    NotificationService notificationService;

    void doIt(Order order) {
        log.info("starting doing order: {}", order);

        this.notificationService.notifyStartedPreparation(order);

        preparing(order);

        log.info("finished doing order: {}", order);
        this.notificationService.notifyFinishedPreparation(order);
    }

    private static void preparing(Order order) {
        try {
            int minutes = ThreadLocalRandom.current().nextInt(1, 5);
            log.info("Preparing the order  {} will take {} minutes", order.getId(), minutes);
            TimeUnit.MINUTES.sleep(minutes);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
