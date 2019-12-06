package io.costax.examplesapi.callbacks;

public class ThirdPartClass {

    public ThirdPartClass() {
        System.out.println("---> In the ThirdPartClass constructor");
    }

    public void init() {
        System.out.println("---> In the ThirdPartClass @PostConstruct");
    }

    public void cleanup() {
        System.out.println("---> In the ThirdPartClass @PreDestroy");

    }
}
