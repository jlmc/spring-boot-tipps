package io.costax.examplesapi.di.control;

import io.costax.examplesapi.di.entity.Client;

public interface Notifier {

    void send(Client client, String message);
}
