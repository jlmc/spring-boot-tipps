package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.GetCommentAuthorUserCase
import io.github.jlmc.firststep.application.port.`in`.GetCommentPostUserCase
import io.github.jlmc.firststep.application.port.`in`.GetCommentsCommand
import io.github.jlmc.firststep.application.port.`in`.GetCommentsUseCase
import io.github.jlmc.firststep.application.port.out.CommentRepository
import io.github.jlmc.firststep.application.port.out.CommentSpecifications
import io.github.jlmc.firststep.domain.Comment
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class CommentService(private val commentRepository: CommentRepository) : GetCommentsUseCase, GetCommentAuthorUserCase,
    GetCommentPostUserCase {

    override fun getComments(command: GetCommentsCommand): List<Comment> {
        return commentRepository.findAll(CommentSpecifications.withAuthorAndPost(command.authorId, command.postId))
    }

    override fun getCommentAuthor(commentId: UUID): User? {
        return commentRepository.getCommentAuthor(commentId)
    }

    override fun getCommentPost(postId: UUID): Post? {
        return commentRepository.getCommentPost(postId)
    }


}
