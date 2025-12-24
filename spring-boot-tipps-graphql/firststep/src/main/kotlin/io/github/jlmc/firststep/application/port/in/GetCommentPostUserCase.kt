package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.Post
import java.util.*

interface GetCommentPostUserCase {
    fun getCommentPost(postId: UUID): Post?
}
