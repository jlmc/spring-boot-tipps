package io.costax.demo.domain.services;

import io.costax.demo.domain.exceptions.BadPasswordException;
import io.costax.demo.domain.exceptions.ResourceNotFoundException;
import io.costax.demo.domain.model.User;
import io.costax.demo.domain.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AddUserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User add(User user) {
        String generatedPwd = "pwd";

        final String encode = passwordEncoder.encode(generatedPwd);

        user.setPwd(encode);

        return userRepository.saveAndFlush(user);
    }

    @Transactional
    public void changePassword(final Integer id, final String currentPassword, final String newPassword) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(User.class, id);
        }

        final User user = userRepository.getOne(id);

        if (!passwordEncoder.matches(currentPassword, user.getPwd())) {
            throw new BadPasswordException("The current Password do not match");
        }

        user.setPwd(passwordEncoder.encode(newPassword));
    }
}
