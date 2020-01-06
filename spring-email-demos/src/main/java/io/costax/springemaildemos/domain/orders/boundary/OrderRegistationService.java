package io.costax.springemaildemos.domain.orders.boundary;

import io.costax.springemaildemos.domain.notifications.EmailSenderService;
import io.costax.springemaildemos.domain.orders.control.OrderRepository;
import io.costax.springemaildemos.domain.orders.control.OrdersConfigurationProperties;
import io.costax.springemaildemos.domain.orders.entity.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.costax.springemaildemos.domain.notifications.EmailSenderService.Message;

@Service
public class OrderRegistationService {

    private final OrderRepository repository;
    private final OrdersConfigurationProperties ordersConfigurationProperties;

    private final EmailSenderService emailSenderService;

    public OrderRegistationService(final OrderRepository repository,
                                   final OrdersConfigurationProperties ordersConfigurationProperties, final EmailSenderService emailSenderService) {
        this.repository = repository;
        this.ordersConfigurationProperties = ordersConfigurationProperties;
        this.emailSenderService = emailSenderService;
    }

    @Transactional
    public Order create(Order request) {
        final Order order = repository.saveAndFlush(request);

        // do other shits ...

        final Message message = Message.builder()
                .from(ordersConfigurationProperties.getEmails().getFrom())
                .to("costajlmpp+test@gmail.com")
                .subject("ping test - " + System.currentTimeMillis())
                .body("ping test - " + System.currentTimeMillis())
                .build();

        emailSenderService.send(message);

        return order;
    }

}
