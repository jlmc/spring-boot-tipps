package io.github.jlmc.firststep.application.port.`in`

import io.github.jlmc.firststep.domain.User
import org.springframework.data.domain.Page

interface GetUsersPageUserCase {

    fun getUsersPage(command: GetUsersPageCommand) : Page<User>
}
