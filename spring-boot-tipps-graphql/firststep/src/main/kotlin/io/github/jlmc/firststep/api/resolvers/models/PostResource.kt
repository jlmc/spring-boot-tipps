package io.github.jlmc.firststep.api.resolvers.models

import io.github.jlmc.firststep.domain.Post
import java.util.*

data class PostResource(val id: UUID, val title: String, val description: String, val votes: Int = 0) {

    constructor(entity: Post) : this(
        id = entity.id!!,
        title = entity.title,
        description = entity.description,
        votes = entity.votes
    )
}
