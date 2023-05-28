package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository : JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment>
