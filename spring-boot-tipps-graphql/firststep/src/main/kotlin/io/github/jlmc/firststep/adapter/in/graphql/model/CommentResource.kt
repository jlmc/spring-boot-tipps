package io.github.jlmc.firststep.adapter.`in`.graphql.model

import io.github.jlmc.firststep.domain.Comment
import java.util.*

@JvmRecord
data class CommentResource(val id: UUID, val text: String) {
    constructor(entity: Comment) : this(id = entity.id!!, text = entity.text)
}
