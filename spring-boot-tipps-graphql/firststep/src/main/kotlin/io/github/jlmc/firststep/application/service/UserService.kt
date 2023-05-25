package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {

    fun getUsers() : List<User> {
        return userRepository.findAll()
    }


}
