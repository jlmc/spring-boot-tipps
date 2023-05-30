package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.UserResource
import io.github.jlmc.firststep.application.port.`in`.GetUserTotalPostsCommand
import io.github.jlmc.firststep.application.port.`in`.GetUserTotalPostsUseCase
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class UserResourcePostsMapping(
    private val postService: PostService,
    private val getUserTotalPostsUser: GetUserTotalPostsUseCase,
) {

    /**
     * field resolver for `post.author.posts`
     */
    @SchemaMapping(typeName = "UserResource")
    fun posts(userResource: UserResource): List<PostResource> {
        return postService.getPostsAuthorId(userResource.id)
            .map(::PostResource)
    }

    @SchemaMapping(typeName = "UserResource")
    fun totalPosts(userResource: UserResource): Long {
        return getUserTotalPostsUser.getUserTotalPosts(GetUserTotalPostsCommand(userId = userResource.id))
    }
}
