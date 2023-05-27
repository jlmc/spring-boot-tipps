package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.AddPostCommand
import io.github.jlmc.firststep.application.port.`in`.AddPostUseCase
import io.github.jlmc.firststep.application.port.out.PostRepository
import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) : AddPostUseCase {

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

    @Transactional
    override fun addPost(command: AddPostCommand): Post {
        val user = userRepository.findById(command.authorId)
            .orElseThrow { IllegalArgumentException("The author \"${command.authorId}\" does not exists") }

        val newPost = Post(title = command.title, description = command.description, author = user)

        postRepository.saveAndFlush(newPost)

        return newPost
    }
}
