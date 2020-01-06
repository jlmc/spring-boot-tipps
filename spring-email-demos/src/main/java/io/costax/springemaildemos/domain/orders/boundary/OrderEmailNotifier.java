package io.costax.springemaildemos.domain.orders.boundary;

import io.costax.springemaildemos.domain.notifications.EmailSenderService;
import io.costax.springemaildemos.domain.orders.control.OrdersConfigurationProperties;
import io.costax.springemaildemos.domain.orders.entity.Order;
import io.costax.springemaildemos.domain.orders.entity.OrderConfirmedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEmailNotifier {

    private final OrdersConfigurationProperties ordersConfigurationProperties;
    private final EmailSenderService emailSenderService;

    public OrderEmailNotifier(final OrdersConfigurationProperties ordersConfigurationProperties, final EmailSenderService emailSenderService) {
        this.ordersConfigurationProperties = ordersConfigurationProperties;
        this.emailSenderService = emailSenderService;
    }

    //@EventListener

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void notify(OrderConfirmedEvent event) {
        final Order order = event.getOrder();

        final EmailSenderService.Message message = EmailSenderService.Message.builder()
                .from(ordersConfigurationProperties.getEmails().getFrom())
                .to("costajlmpp+test@gmail.com")
                .subject("ping test - " + System.currentTimeMillis())
                .body("ping test - " + System.currentTimeMillis())

                .templateName("order-confirmed.html")
                .templateParam("clientName", "Duke the Greater")

                .build();

        emailSenderService.send(message);

    }
}
