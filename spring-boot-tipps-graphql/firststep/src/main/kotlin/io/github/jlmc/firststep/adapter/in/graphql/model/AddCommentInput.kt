package io.github.jlmc.firststep.adapter.`in`.graphql.model

import java.util.*

@JvmRecord
data class AddCommentInput(val authorId: UUID, val postId: UUID, val text: String)
