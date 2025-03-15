package io.github.jlmc.pizzacondo.processing.service.application.port.output;

import io.github.jlmc.pizzacondo.processing.service.domain.model.Order;

public interface NotificationService {

    void notifyStartedPreparation(Order order);
    void notifyFinishedPreparation(Order order);
}
