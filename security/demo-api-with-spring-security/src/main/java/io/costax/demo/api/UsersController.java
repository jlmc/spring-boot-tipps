package io.costax.demo.api;

import io.costax.demo.domain.model.User;
import io.costax.demo.domain.repositories.UserRepository;
import io.costax.demo.domain.services.AddUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/users",
        produces = {
                MediaType.APPLICATION_JSON_VALUE
        })
public class UsersController {

    @Autowired
    AddUserService addUserService;

    @Autowired
    UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        final User added = addUserService.add(user);

        return ResponseEntity.created(ResourceUriHelper.getUri(added.getId())).body(added);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Integer id) {
        return userRepository.getByIdWithPeAndPermissions(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<User> list() {
        return userRepository.getUsersWithPermissions();
    }
}
