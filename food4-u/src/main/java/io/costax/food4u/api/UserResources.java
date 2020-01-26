package io.costax.food4u.api;

import io.costax.food4u.api.assembler.users.UserAssembler;
import io.costax.food4u.api.assembler.users.UserRepresentationModelAssembler;
import io.costax.food4u.api.model.users.input.PasswordInputRepresentation;
import io.costax.food4u.api.model.users.input.UserInputRepresentation;
import io.costax.food4u.api.model.users.output.UserOutputRepresentation;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.User;
import io.costax.food4u.domain.repository.UserRepository;
import io.costax.food4u.domain.services.UserRegistrationService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

import static io.costax.food4u.domain.repository.UserSpecifications.orderByIdAndEmail;

@RestController
@RequestMapping("/users")
public class UserResources {

    private final UserRepository userRepository;
    private final UserRegistrationService userRegistrationService;
    private final UserRepresentationModelAssembler userRepresentationModelAssembler;
    private final UserAssembler userAssembler;

    public UserResources(final UserRepository userRepository,
                         final UserRegistrationService userRegistrationService,
                         final UserRepresentationModelAssembler userRepresentationModelAssembler,
                         final UserAssembler userAssembler) {
        this.userRepository = userRepository;
        this.userRegistrationService = userRegistrationService;
        this.userRepresentationModelAssembler = userRepresentationModelAssembler;
        this.userAssembler = userAssembler;
    }

    @GetMapping
    public CollectionModel<UserOutputRepresentation> list() {
        return userRepresentationModelAssembler.toCollectionModel(userRepository.findAll(orderByIdAndEmail()));
    }

    @GetMapping("/{userId}")
    public UserOutputRepresentation getById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(userRepresentationModelAssembler::toModel)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

    }

    @PostMapping
    public ResponseEntity<UserOutputRepresentation> add(@RequestBody @Valid UserInputRepresentation payload,
                                                        UriComponentsBuilder b) {
        final User user = userAssembler.toDomainObject(payload);
        User saved = userRegistrationService.sign(user);

        //UriComponents uriComponents = b.path("/cookers/{id}").buildAndExpand(saved.getId());
        //final URI uri = uriComponents.toUri();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(userRepresentationModelAssembler.toModel(saved));
    }

    @PutMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long userId, @RequestBody @Valid PasswordInputRepresentation payload) {
        userRegistrationService.changePassword(userId, payload.getCurrentPassword(), payload.getNewPassword());
    }
}
