package io.costax.examplesapi.callbacks;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class CallbackBean {

    public CallbackBean() {
        System.out.println("---> In the CallbackBean constructor");
    }

    @PostConstruct
    public void init() {
        System.out.println("---> In the CallbackBean @PostConstruct");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("---> In the CallbackBean @PreDestroy");

    }
}
