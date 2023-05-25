package io.github.jlmc.firststep.api.resolvers.posts

import io.github.jlmc.firststep.application.service.PostService
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class PostResolver(
    private val postService: PostService,
) {

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
