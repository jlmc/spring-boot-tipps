package io.costax.examplesapi.di.control;

import io.costax.examplesapi.di.entity.Client;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Clients {

    private Map<Long, Client> table = new ConcurrentHashMap<>();

    @PostConstruct
    protected void init() {
        System.out.println("-- starting default");
        final Client example1 = Client.of(1L, "example1", "example1@liamg.com");
        table.put(example1.getId(), example1);
        final Client example2 = Client.of(2L, "example2", "example2@liamg.com");
        table.put(example2.getId(), example2);
    }


    public Client getOne(final Long id) {
        return table.get(id);
    }
}
