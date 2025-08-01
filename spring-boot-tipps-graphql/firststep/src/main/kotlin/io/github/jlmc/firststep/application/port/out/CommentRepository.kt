package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.Comment
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository : JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment> {

    @Query(value = "select c.author from Comment c where c.id = :commentId")
    fun getCommentAuthor(@Param("commentId") commentId: UUID): User?

    @Query(value = "select c.post from Comment c where c.id = :postId")
    fun getCommentPost(@Param("postId") postId: UUID): Post?

    @Query(value = "select c from Comment c left join fetch c.post p where p.id = :postId")
    fun getCommentsByPostId(@Param("postId") postId: UUID) : List<Comment>

    @Query(value = "select c from Comment c left join fetch c.post p left join fetch p.author left join fetch c.author where p.id in :postIds")
    fun getCommentsByPostIds(@Param("postIds") postIds: Collection<UUID>) : List<Comment>
}
