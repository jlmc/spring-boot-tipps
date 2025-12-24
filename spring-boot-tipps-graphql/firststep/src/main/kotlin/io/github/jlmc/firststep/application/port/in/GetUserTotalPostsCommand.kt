package io.github.jlmc.firststep.application.port.`in`

import java.util.*

@JvmRecord
data class GetUserTotalPostsCommand(val userId: UUID)
