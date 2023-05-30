package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID> {
    @Query(value = "select p.author from Post p where p.id = :postId")
    fun findPostAuthor(@Param("postId") postId: UUID): User?

    @Query("select p from Post p where p.author.id = :authorId order by p.createdDate asc")
    fun getPostsAuthorId(@Param("authorId") authorId: UUID): List<Post>
}
