package io.github.jlmc.firststep.application.port.`in`

import java.util.*

@JvmRecord
data class AddCommentCommand(val authorId: UUID, val postId: UUID, val text: String)
