package io.github.jlmc.firststep.adapter.`in`.graphql

import io.github.jlmc.firststep.adapter.`in`.graphql.model.PostResource
import io.github.jlmc.firststep.application.service.PostService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
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

}
