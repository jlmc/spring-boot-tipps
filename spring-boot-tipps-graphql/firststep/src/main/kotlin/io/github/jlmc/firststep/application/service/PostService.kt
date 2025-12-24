package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.AddPostCommand
import io.github.jlmc.firststep.application.port.`in`.AddPostUseCase
import io.github.jlmc.firststep.application.port.out.CommentRepository
import io.github.jlmc.firststep.application.port.out.PostRepository
import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.Comment
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
class PostService(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
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

    fun getPostComments(postId: UUID): List<Comment> {
        return commentRepository.getCommentsByPostId(postId)
    }

    fun getPostsComments(postIds: Collection<UUID>): List<Comment> = when {
        postIds.isEmpty() -> emptyList()
        else -> commentRepository.getCommentsByPostIds(postIds)
    }

    @Transactional
    override fun addPost(command: AddPostCommand): Post {
        val user = userRepository.findById(command.authorId)
            .orElseThrow { IllegalArgumentException("The author \"${command.authorId}\" does not exists") }

        val newPost = Post(title = command.title, description = command.description, author = user)

        postRepository.saveAndFlush(newPost)

        return newPost
    }

    fun getPostsByAuthorIds(authorIds: Set<UUID>): Map<User, List<Post>> {
        return authorIds
            .takeIf { it.isNotEmpty() }
            ?.let(postRepository::getPostByAuthorIdIn)
            ?.groupBy(keySelector = { it -> it.author }, valueTransform = { it })
            ?.map { entry ->
                entry.key to entry.value.sortedBy { it.createdDate }
            }?.toMap() ?: emptyMap()
    }

    fun getTotalPostByAuthor(userIds: Set<UUID>): Map<UUID, Long> {
        return this.postRepository.getTotalPostByAuthor(userIds).associate {
            it.first.id!! to it.second
        }
    }


}
