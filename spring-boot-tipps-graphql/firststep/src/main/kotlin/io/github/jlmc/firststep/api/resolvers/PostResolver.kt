package io.github.jlmc.firststep.api.resolvers

import io.github.jlmc.firststep.api.resolvers.models.PostResource
import io.github.jlmc.firststep.api.resolvers.models.UserResource
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class PostResolver(
    private val postService: PostService,
) {

    @QueryMapping
    fun mostPopularPosts(@Argument page: Int = 0, @Argument size: Int): List<PostResource> {
        val pageable: Pageable = PageRequest.of(page, size)
        return postService.mostPopularPosts(pageable).content.map(::PostResource)
    }

    @QueryMapping
    fun getPosts(): List<PostResource> {
        return postService.getPosts().map(::PostResource)
    }

    /**
     * this functions it is field resolver, it needs to be named as the name of the fields in the schema post.author
     * the typeName defines who is the parent of this field resolver, remember that the field resolver method must receive always the parent
     */
    @SchemaMapping(typeName = "PostResource") // the typeName defines who is the parent of this field resolver
    fun author(postResource: PostResource): UserResource {
        return postService.getAuthorByPostId(postResource.id)?.let(::UserResource) ?: throw IllegalStateException()
    }

    /**
     * field resolver for `post.author.posts`
     */
    @SchemaMapping(typeName = "UserResource")
    fun posts(userResource: UserResource): List<PostResource> {
        return postService.getPostsAuthorId(userResource.id).map(::PostResource)
    }
}
