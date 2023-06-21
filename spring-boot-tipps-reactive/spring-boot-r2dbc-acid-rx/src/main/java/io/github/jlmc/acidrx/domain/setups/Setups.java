package io.github.jlmc.acidrx.domain.setups;

import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
//@Transactional
public class Setups implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(Setups.class);

    private final UserRepository userRepository;

    public Setups(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        userRepository.count()
                .filter(count -> count < 1L)
                .flatMap(count -> {
                    var users =
                            List.of(
                                    new XUser(new UUID(0, 1L), "Duke"),
                                    new XUser(new UUID(0, 2L), "Roger"),
                                    new XUser(new UUID(0, 3L), "Mickael"));
                    return Mono.just(users);
                })
                .flatMapMany(userRepository::saveAll)
                .thenMany(it -> userRepository.findAll())
                .subscribe(this::onSuccess, this::onFailed, this::onFinished);
    }

    private void onFinished() {
        LOGGER.info("initialization is done...");
    }

    private void onFailed(Throwable throwable) {
        LOGGER.error("error, " + throwable);
    }

    private void onSuccess(Object o) {
        LOGGER.info("users: <{}>", o);
    }

    private static class XUser extends User {

        public XUser(UUID id, String name) {
            super(id, name);
        }

        @Override
        public boolean isNew() {
            return true;
        }
    }
}
