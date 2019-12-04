package io.costax.examplesapi.di.boundary;

import io.costax.examplesapi.di.control.Clients;
import io.costax.examplesapi.di.control.Notifier;
import io.costax.examplesapi.di.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientActivator {

    private final Clients clients;
    private final List<Notifier> notifiers;

    public ClientActivator(final Clients clients, final List<Notifier> notifiers) {
        this.clients = clients;
        this.notifiers = notifiers;
    }


    public void active(long clientId) {
        final Client one = clients.getOne(clientId);

        // TODO: 04/12/2019 - we can do some check state validations

        one.enable();
        notifiers.forEach(n -> n.send(one, String.format("The [%s] has been enable at [%s]", one.getId(), System.currentTimeMillis())));
    }
}
