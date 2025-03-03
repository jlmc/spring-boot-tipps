package io.github.jlmc.pizzacondo.om.service.application.port.output;

import io.github.jlmc.pizzacondo.om.service.domain.model.Order;

public interface NotificationService {

    void orderPlaced(Order order);

    void orderAccepted(Order order);

}
