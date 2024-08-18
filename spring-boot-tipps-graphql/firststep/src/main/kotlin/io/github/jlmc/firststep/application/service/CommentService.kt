package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.*
import io.github.jlmc.firststep.application.port.out.CommentRepository
import io.github.jlmc.firststep.application.port.out.CommentSpecifications
import io.github.jlmc.firststep.application.port.out.PostRepository
import io.github.jlmc.firststep.application.port.out.UserRepository
import io.github.jlmc.firststep.domain.Comment
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
) : GetCommentsUseCase, GetCommentAuthorUserCase,
    GetCommentPostUserCase, AddCommentUserCase {

    override fun getComments(command: GetCommentsCommand): List<Comment> {
        return commentRepository.findAll(CommentSpecifications.withAuthorAndPost(command.authorId, command.postId))
    }

    override fun getCommentAuthor(commentId: UUID): User? {
        return commentRepository.getCommentAuthor(commentId)
    }

    override fun getCommentPost(postId: UUID): Post? {
        return commentRepository.getCommentPost(postId)
    }

    @Transactional
    override fun addComment(command: AddCommentCommand): Comment {
        val post = postRepository.getReferenceById(command.postId) ?: throw IllegalArgumentException("invalid post id")
        val user =
            userRepository.getReferenceById(command.authorId) ?: throw IllegalArgumentException("invalid author id")

        val comment = Comment(text = command.text, post = post, author = user)

        commentRepository.saveAndFlush(comment)

        return comment;
    }
}
