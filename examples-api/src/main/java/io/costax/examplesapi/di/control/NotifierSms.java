package io.costax.examplesapi.di.control;

import io.costax.examplesapi.di.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class NotifierSms implements Notifier {

    public NotifierSms() {
        System.out.println("--- NotifierSms starting");
    }

    @Override
    public void send(final Client client, final String message) {
        System.out.printf("--- Notifying %s via SMS %s : %s\n",
                client.getName(), client.getEmail(), message);
    }
}
