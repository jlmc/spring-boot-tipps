package io.github.jlmc.firststep.api.resolvers.posts

import io.github.jlmc.firststep.api.resolvers.models.AddUserInput
import io.github.jlmc.firststep.application.port.`in`.AddUserUseCase
import io.github.jlmc.firststep.application.port.`in`.CreateUserCommand
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller

@Controller
class UserResolver(val addUserUseCase: AddUserUseCase) {

    @MutationMapping
    fun addUser(@Argument addUserInput: AddUserInput) :String {
        return addUserUseCase.createUser(CreateUserCommand(addUserInput.name))
    }
}
