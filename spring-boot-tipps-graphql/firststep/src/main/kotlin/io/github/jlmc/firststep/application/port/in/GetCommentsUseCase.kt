package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.Comment

interface GetCommentsUseCase {
    fun getComments(command: GetCommentsCommand): List<Comment>
}
