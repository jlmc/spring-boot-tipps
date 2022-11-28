package io.costax.examplesapi.di.control;

import io.costax.examplesapi.di.entity.Client;

//@Component
public class NotifierEmail implements Notifier {

    private final String smtpHost;
    private final int priority;

    public NotifierEmail(final String smtpHost, final int priority) {
        System.out.println("--- NotifierEmail starting");
        this.smtpHost = smtpHost;
        this.priority = priority;
    }

    @Override
    public void send(final Client client, final String message) {
        System.out.printf("--- Notifying %s via Email %s : %s\n",
                client.getName(), client.getEmail(), message);
    }
}
