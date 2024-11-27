package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.User
import java.util.*

interface GetCommentAuthorUserCase {
    fun getCommentAuthor(commentId: UUID): User?
}
