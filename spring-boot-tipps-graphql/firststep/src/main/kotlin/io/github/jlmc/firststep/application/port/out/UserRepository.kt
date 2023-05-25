package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID>
