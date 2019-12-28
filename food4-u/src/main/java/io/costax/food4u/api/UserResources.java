package io.costax.food4u.api;

import io.costax.food4u.api.assembler.users.UserAssembler;
import io.costax.food4u.api.model.users.input.PasswordInputRepresentation;
import io.costax.food4u.api.model.users.input.UserInputRepresentation;
import io.costax.food4u.api.model.users.output.UserOutputRepresentation;
import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.model.User;
import io.costax.food4u.domain.repository.UserRepository;
import io.costax.food4u.domain.services.UserRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static io.costax.food4u.domain.repository.UserSpecifications.orderByIdAndEmail;

@RestController
@RequestMapping("/users")
public class UserResources {

    private final UserRepository userRepository;
    private final UserRegistrationService userRegistrationService;
    private final UserAssembler userAssembler;

    public UserResources(final UserRepository userRepository,
                         final UserRegistrationService userRegistrationService,
                         final UserAssembler userAssembler) {
        this.userRepository = userRepository;
        this.userRegistrationService = userRegistrationService;
        this.userAssembler = userAssembler;
    }

    @GetMapping
    public List<?> list() {
        return userAssembler.toListOfRepresentations(userRepository.findAll(orderByIdAndEmail()));
    }

    @GetMapping("/{userId}")
    public UserOutputRepresentation getById(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(userAssembler::toRepresentation)
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

        return ResponseEntity.created(location).body(userAssembler.toRepresentation(saved));
    }

    @PutMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long userId, @RequestBody @Valid PasswordInputRepresentation payload) {
        userRegistrationService.changePassword(userId, payload.getCurrentPassword(), payload.getNewPassword());
    }
}
