package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query(value = "select count(p.id) from User u inner join u.posts p where u.id = :userId")
    fun countUserPosts(userId: UUID): Long
}
