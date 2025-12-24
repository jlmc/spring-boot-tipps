package io.github.jlmc.firststep.application.port.`in`

import java.util.*

data class GetCommentsCommand(val postId: UUID?, val authorId: UUID?)
