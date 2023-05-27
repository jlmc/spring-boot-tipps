package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.*
import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) :
    AddUserUseCase,
    GetUsersPageUserCase,
    GetUserTotalPostsUseCase {

    @Transactional
    override fun createUser(command: CreateUserCommand): String {
        val user = userRepository.saveAndFlush(User(name = command.name))

        return user.id?.toString() ?: throw RuntimeException()
    }

    override fun getUsersPage(command: GetUsersPageCommand): Page<User> {
        return userRepository.findAll(PageRequest.of(command.page, command.size, command.sort))
    }

    override fun getUserTotalPosts(command: GetUserTotalPostsCommand) : Long {


        return userRepository.countUserPosts(command.userId)
    }

}
