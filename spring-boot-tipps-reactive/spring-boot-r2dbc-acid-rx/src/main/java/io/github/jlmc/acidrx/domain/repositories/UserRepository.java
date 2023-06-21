package io.github.jlmc.acidrx.domain.repositories;

import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.exceptions.ResourceNotFoundException;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepository extends R2dbcRepository<User, UUID> {


    default Mono<User> findUser(String userId) {
        return findById(UUID.fromString(userId))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("user '" + userId + "' not found")));
    }
}
