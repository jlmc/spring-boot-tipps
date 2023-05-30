package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.AddPostInput
import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.application.port.`in`.AddPostCommand
import io.github.jlmc.firststep.application.port.`in`.AddPostUseCase
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
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
