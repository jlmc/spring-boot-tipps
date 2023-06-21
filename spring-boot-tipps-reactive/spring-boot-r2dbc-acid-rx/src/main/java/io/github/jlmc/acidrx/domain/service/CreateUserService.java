package io.github.jlmc.acidrx.domain.service;

import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CreateUserService {

    private final UserRepository userRepository;

    public CreateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<UUID> create(CreateUserCommand command) {
        return userRepository.save(new User(command.name())).map(User::getId);
    }
}
