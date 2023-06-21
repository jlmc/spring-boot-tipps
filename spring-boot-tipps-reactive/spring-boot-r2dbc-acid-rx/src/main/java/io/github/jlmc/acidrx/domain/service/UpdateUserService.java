package io.github.jlmc.acidrx.domain.service;

import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class UpdateUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserService.class);

    private final UserRepository userRepository;

    public UpdateUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Mono<User> execute(UpdateUserCommand command) {
        return userRepository.findUser(command.userId())
                .doFirst(() -> LOGGER.info("trying to find user <{}>", command.userId()))
                .doOnSuccess(foundUser -> LOGGER.info("found user <{}> successful", foundUser.getId()))
                .map(user -> {

                    user.setName(command.name());
                    user.setNotes(command.notes());

                    return user;
                })
                .flatMap(userRepository::save)
                .doFirst(() -> LOGGER.info("trying to save the user with the required changes <{}>", command.userId()))
                .doOnSuccess(savedUser -> LOGGER.info("saved the user <{}> successful", savedUser));

    }

}
