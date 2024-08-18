package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.AddPostInput
import io.github.jlmc.firststep.adapter.`in`.graphql.model.CommentResource
import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.application.port.`in`.AddPostCommand
import io.github.jlmc.firststep.application.port.`in`.AddPostUseCase
import io.github.jlmc.firststep.application.service.PostService
import io.github.jlmc.firststep.domain.Comment
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.BatchMapping
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class PostResolver(
    private val postService: PostService,
    private val addPostUseCase: AddPostUseCase,
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

    /*
    @SchemaMapping(typeName = "PostResource", field = "comments")
    fun comments(postResource: PostResource): List<CommentResource> {
        return postService.getPostComments(postResource.id).map(::CommentResource)
    }
     */

    @BatchMapping(
        typeName = "PostResource",
        field = "comments"
    )
    fun batchPostComments(postResources: List<PostResource>): Map<PostResource, List<CommentResource>> {
        val postResourceByIdCache =
            postResources.toSet().associateBy(PostResource::id)

        val postComments =
            postService.getPostsComments(postResources.map(PostResource::id))
                .groupBy(Comment::post)

        return postComments.entries.associate {
            Pair(
                postResourceByIdCache[it.key.id]!!,
                it.value.map(::CommentResource)
            )
        }
    }

    @MutationMapping
    fun addPost(@Argument(name = "addPostInput") input: AddPostInput): PostResource {
        return addPostUseCase.addPost(
            AddPostCommand(
                title = input.title,
                description = input.description,
                authorId = input.authorId
            )
        ).let(::PostResource)
    }

}
