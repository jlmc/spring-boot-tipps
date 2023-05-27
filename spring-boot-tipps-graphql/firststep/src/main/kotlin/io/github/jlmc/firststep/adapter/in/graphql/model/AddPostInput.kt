package io.github.jlmc.firststep.adapter.`in`.graphql.model

import java.util.UUID

@JvmRecord
data class AddPostInput(val title: String, val description: String, val authorId: UUID)
