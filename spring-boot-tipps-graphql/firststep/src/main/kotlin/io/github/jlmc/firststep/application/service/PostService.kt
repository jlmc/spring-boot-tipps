package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.out.PostRepository
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class PostService(private val postRepository: PostRepository) {

    fun getPosts() : List<Post> {
        return postRepository.findAll()
    }

    fun getAuthorByPostId(postId: UUID): User? {
        return postRepository.findPostAuthor(postId)
    }

    fun getPostsAuthorId(authorId: UUID): List<Post> {
        return postRepository.getPostsAuthorId(authorId)
    }
}
