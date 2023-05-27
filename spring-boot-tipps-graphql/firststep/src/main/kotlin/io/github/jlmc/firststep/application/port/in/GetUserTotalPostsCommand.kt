package io.github.jlmc.firststep.application.port.`in`

import java.util.UUID

@JvmRecord
data class GetUserTotalPostsCommand(val userId: UUID)
