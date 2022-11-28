package io.github.jlmc.aop.domain.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdGenerator {

    private final AtomicInteger count = new AtomicInteger(0);

    public Integer generate() {
        return count.incrementAndGet();
    }
}
