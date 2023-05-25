package io.github.jlmc.firststep.api.resolvers.posts

import java.util.UUID

data class PostResource(val id: UUID, val title: String, val description: String)
