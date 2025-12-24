package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.Comment

interface AddCommentUserCase {

    fun addComment(command: AddCommentCommand): Comment
}
