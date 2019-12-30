package io.costax.food4u.domain.services;

import io.costax.food4u.domain.exceptions.ResourceNotFoundException;
import io.costax.food4u.domain.exceptions.UserWithSameEmailAlreadyExistsException;
import io.costax.food4u.domain.model.User;
import io.costax.food4u.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserRegistrationService {

    private final UserRepository userRepository;

    private final PasswordGenerator passwordGenerator;

    public UserRegistrationService(final UserRepository userRepository, final PasswordGenerator passwordGenerator) {
        this.userRepository = userRepository;
        this.passwordGenerator = passwordGenerator;
    }

    @Transactional
    public User sign(User user) {
        // validate if some user with the same email is already in use
        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw UserWithSameEmailAlreadyExistsException.of(user.getEmail());
        }

        String generatedStartedPassword = passwordGenerator.generate();
        user.changePassword(null, generatedStartedPassword);

        userRepository.save(user);
        userRepository.flush();

        return user;
    }

    @Transactional
    public void changePassword(final Long userId, final String currentPassword, final String newPassword) {

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, userId));

        user.changePassword(currentPassword, newPassword);

        userRepository.flush();
    }
}
