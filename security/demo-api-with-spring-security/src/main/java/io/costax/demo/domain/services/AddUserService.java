package io.costax.demo.domain.services;

import io.costax.demo.domain.model.User;
import io.costax.demo.domain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddUserService {

    @Autowired
    UserRepository userRepository;

    @Transactional
    public User add(User user) {
        final User user1 = userRepository.saveAndFlush(user);


        return user1;
    }
}
