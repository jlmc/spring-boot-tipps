package io.github.jlmc.acidrx.domain.repositories;

import io.github.jlmc.acidrx.domain.entities.User;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Mono;

import java.util.UUID;

//@Component
public class UserBeforeConvertCallback implements BeforeConvertCallback<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserBeforeConvertCallback.class);

    @Override
    public Publisher<User> onBeforeConvert(User entity, SqlIdentifier table) {
        LOGGER.info("before converting user: <{}>", entity);

        if (entity.getId() == null) {
            UUID id = UUID.randomUUID();
            LOGGER.info("Before converting user: <{}> this entity is new, the id <{}>, is going to be assign", entity, id);
            entity.setId(id);
        }

        return Mono.just(entity);
    }
}
