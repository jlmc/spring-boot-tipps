package io.github.jlmc.firststep.application.port.`in`

import org.springframework.data.domain.Sort

@JvmRecord
data class GetUsersPageCommand(
    val page: Int,
    val size: Int,
    val sort: Sort = Sort.by(Sort.Order.asc("name"))
)
