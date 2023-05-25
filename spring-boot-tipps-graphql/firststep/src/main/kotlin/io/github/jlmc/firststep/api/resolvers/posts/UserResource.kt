package io.github.jlmc.firststep.api.resolvers.posts

import io.github.jlmc.firststep.domain.User
import java.util.*

data class UserResource(val id: UUID, val name: String) {

    constructor(entity: User) : this(id = entity.id!!, name = entity.name)
}
