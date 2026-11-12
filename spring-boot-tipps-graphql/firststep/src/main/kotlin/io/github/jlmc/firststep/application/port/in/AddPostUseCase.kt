package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.Post

interface AddPostUseCase {

    fun addPost(command: AddPostCommand): Post
}
