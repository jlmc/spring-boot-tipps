package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.AddUserInput
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UserResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UsersPage
import io.github.jlmc.firststep.application.port.`in`.AddUserUseCase
import io.github.jlmc.firststep.application.port.`in`.CreateUserCommand
import io.github.jlmc.firststep.application.port.`in`.GetUsersPageCommand
import io.github.jlmc.firststep.application.port.`in`.GetUsersPageUserCase
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserResolver(
    val addUserUseCase: AddUserUseCase,
    val getUsersPageUserCase: GetUsersPageUserCase,
    val postService: PostService
) {

    @MutationMapping
    fun addUser(@Argument addUserInput: AddUserInput): String {
        return addUserUseCase.createUser(CreateUserCommand(addUserInput.name))
    }

    @QueryMapping
    fun getUsers(@Argument page: Int, @Argument size: Int): UsersPage {
        return getUsersPageUserCase.getUsersPage(GetUsersPageCommand(page, size))
            .map(::UserResource).let { pageResult ->
                UsersPage(
                    content = pageResult.content,
                    pageNumber = pageResult.number,
                    pageSize = pageResult.size,
                    totalPages = pageResult.totalPages,
                    isFirst = pageResult.isFirst,
                    isLast = pageResult.isLast
                )
            }
    }
}


