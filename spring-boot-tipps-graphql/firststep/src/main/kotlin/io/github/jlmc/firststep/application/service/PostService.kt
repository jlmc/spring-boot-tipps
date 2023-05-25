package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.out.PostRepository
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class PostService(private val postRepository: PostRepository) {

    fun getPosts(): List<Post> {
        return postRepository.findAll()
    }

    fun getAuthorByPostId(postId: UUID): User? {
        return postRepository.findPostAuthor(postId)
    }

    fun getPostsAuthorId(authorId: UUID): List<Post> {
        return postRepository.getPostsAuthorId(authorId)
    }

    fun mostPopularPosts(pageable: Pageable): Page<Post> {
        val pageableSorted =
            PageRequest.of(pageable.pageNumber, pageable.pageSize, Sort.by(Sort.Order.desc("votes")))

        return postRepository.findAll(pageableSorted)
    }
}
