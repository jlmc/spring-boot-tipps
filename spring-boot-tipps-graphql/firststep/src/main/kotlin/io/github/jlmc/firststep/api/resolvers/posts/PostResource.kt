package io.github.jlmc.firststep.api.resolvers.posts

import io.github.jlmc.firststep.domain.Post
import java.util.*

data class PostResource(val id: UUID, val title: String, val description: String) {

    constructor(entity: Post) : this(id = entity.id!!, title = entity.title, description = entity.description)
}
