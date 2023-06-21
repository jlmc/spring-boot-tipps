package io.github.jlmc.acidrx.api;

import io.github.jlmc.acidrx.api.mappers.UserResourceMapper;
import io.github.jlmc.acidrx.api.model.requests.CreateUserRequest;
import io.github.jlmc.acidrx.api.model.requests.UpdateUserRequest;
import io.github.jlmc.acidrx.api.model.response.UserResource;
import io.github.jlmc.acidrx.domain.entities.User;
import io.github.jlmc.acidrx.domain.repositories.UserRepository;
import io.github.jlmc.acidrx.domain.service.CreateUserCommand;
import io.github.jlmc.acidrx.domain.service.CreateUserService;
import io.github.jlmc.acidrx.domain.service.UpdateUserCommand;
import io.github.jlmc.acidrx.domain.service.UpdateUserService;
import io.github.jlmc.acidrx.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Validated
@RestController
@RequestMapping(
        value = "/users",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
public class UsersController {

    private final UserRepository userRepository;
    private final CreateUserService createUserService;
    private final UpdateUserService updateUserService;
    private final UserResourceMapper userResourceMapper;

    public UsersController(UserRepository userRepository,
                           CreateUserService createUserService,
                           UpdateUserService updateUserService,
                           UserResourceMapper userResourceMapper) {
        this.userRepository = userRepository;
        this.createUserService = createUserService;
        this.updateUserService = updateUserService;
        this.userResourceMapper = userResourceMapper;
    }

    @GetMapping("/{id}")
    public Mono<UserResource> usersById(@PathVariable("id") String id) {
        return Mono.just(id)
                .map(UUID::fromString)
                .flatMap(userRepository::findById)
                .map(userResourceMapper::userToUserResource)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User <" + id + "> not found.")));
    }

    @GetMapping
    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<UserResource>> create(@Validated @RequestBody CreateUserRequest requestPayload,
                                                     ServerHttpRequest request) {
        return createUserService.create(new CreateUserCommand(requestPayload.name()))
                .flatMap(userRepository::findById)
                .map(userResourceMapper::userToUserResource)
                .map(it -> {
                    var location = UriComponentsBuilder.fromHttpRequest(request)
                            .path("/{id}").build(it.id());
                    return ResponseEntity.created(location).body(it);
                });
    }

    @PutMapping(path = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> update(@PathVariable("userId") String userId, @Validated @RequestBody UpdateUserRequest payload) {
        return updateUserService.execute(new UpdateUserCommand(userId, payload.name(), payload.notes())).then();
    }
}
