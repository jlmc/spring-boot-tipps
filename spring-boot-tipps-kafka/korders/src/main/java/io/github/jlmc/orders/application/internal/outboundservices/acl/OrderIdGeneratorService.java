package io.github.jlmc.orders.application.internal.outboundservices.acl;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class OrderIdGeneratorService {

    public String generate() {
        //ThreadLocalRandom current = ThreadLocalRandom.current();
        String random = UUID.randomUUID().toString().toLowerCase();
        return random.substring(0, random.indexOf("-"));
    }
}
