package io.github.jlmc.firststep.application.service

import io.github.jlmc.firststep.application.port.`in`.GetCommentsCommand
import io.github.jlmc.firststep.application.port.`in`.GetCommentsUseCase
import io.github.jlmc.firststep.application.port.out.CommentRepository
import io.github.jlmc.firststep.application.port.out.CommentSpecifications
import io.github.jlmc.firststep.domain.Comment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(private val commentRepository: CommentRepository) : GetCommentsUseCase {

    override fun getComments(command: GetCommentsCommand): List<Comment> {
        return commentRepository.findAll(CommentSpecifications.withAuthorAndPost(command.authorId, command.postId))
    }
}
