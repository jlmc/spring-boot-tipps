package io.costax.examplesapi.di.boundary;

import io.costax.examplesapi.di.control.Clients;
import io.costax.examplesapi.di.control.Notifier;
import io.costax.examplesapi.di.entity.Client;
import io.costax.examplesapi.tennacy.TenancyMigration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientActivator {

    private final Clients clients;
    private final List<Notifier> notifiers;

    // @Autowired(required = false)
    TenancyMigration tenancyMigration;

    //@Autowired(required = false)
    public ClientActivator(final Clients clients,
                           final List<Notifier> notifiers,
                           @Autowired(required = false) TenancyMigration tenancyMigration) {
        this.clients = clients;
        this.notifiers = notifiers;
        this.tenancyMigration = tenancyMigration;
    }


    public void active(long clientId) {
        final Client client = clients.getOne(clientId);

        // TODO: 04/12/2019 - we can do some check state validations

        client.enable();

        if (tenancyMigration != null) {
            tenancyMigration.execute(client);
        }

        notifiers.forEach(n -> n.send(client, String.format("#### The [%s] has been enable at [%s]", client.getId(), System.currentTimeMillis())));

    }
}
