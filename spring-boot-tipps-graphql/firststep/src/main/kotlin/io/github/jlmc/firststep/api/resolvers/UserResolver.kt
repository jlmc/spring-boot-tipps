package io.github.jlmc.firststep.api.resolvers

import io.github.jlmc.firststep.api.resolvers.models.AddUserInput
import io.github.jlmc.firststep.api.resolvers.models.PostResource
import io.github.jlmc.firststep.api.resolvers.models.UserResource
import io.github.jlmc.firststep.api.resolvers.models.UsersPage
import io.github.jlmc.firststep.application.port.`in`.AddUserUseCase
import io.github.jlmc.firststep.application.port.`in`.CreateUserCommand
import io.github.jlmc.firststep.application.port.`in`.GetUsersPageCommand
import io.github.jlmc.firststep.application.port.`in`.GetUsersPageUserCase
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
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
            .map(::UserResource).let { page ->
                UsersPage(
                    content = page.content,
                    pageNumber = page.number,
                    pageSize = page.size,
                    totalPages = page.totalPages,
                    isFirst = page.isFirst,
                    isLast = page.isLast
                )
            }
    }

    /**
     * field resolver for `post.author.posts`
     */
    @SchemaMapping(typeName = "UserResource")
    fun posts(userResource: UserResource): List<PostResource> {
        return postService.getPostsAuthorId(userResource.id)
            .map(::PostResource)
    }
}


