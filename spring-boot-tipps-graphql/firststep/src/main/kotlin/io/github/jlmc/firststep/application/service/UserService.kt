package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.AddUserUseCase
import io.github.jlmc.firststep.application.port.`in`.CreateUserCommand
import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) : AddUserUseCase {

    @Transactional
    override fun createUser(command: CreateUserCommand): String {
        val user = userRepository.saveAndFlush(User(name = command.name))

        return user.id?.toString() ?: throw RuntimeException()
    }
}
