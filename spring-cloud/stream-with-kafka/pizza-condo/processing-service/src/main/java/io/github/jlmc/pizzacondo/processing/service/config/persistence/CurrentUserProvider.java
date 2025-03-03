package io.github.jlmc.pizzacondo.processing.service.config.persistence;

public interface CurrentUserProvider {

    String DEFAULT_USER = "Anonymous";

    default String getCurrentUserName() {
        return DEFAULT_USER;
    }

}
