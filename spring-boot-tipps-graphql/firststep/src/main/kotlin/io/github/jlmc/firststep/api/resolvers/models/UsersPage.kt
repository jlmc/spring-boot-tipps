package io.github.jlmc.firststep.api.resolvers.models

@JvmRecord
data class UsersPage(
    val content: List<UserResource>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalPages: Int,
    val isFirst: Boolean,
    val isLast: Boolean
)
