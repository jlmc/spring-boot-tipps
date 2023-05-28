package io.github.jlmc.firststep.application.port.out

import io.github.jlmc.firststep.domain.Comment
import io.github.jlmc.firststep.domain.Post
import io.github.jlmc.firststep.domain.User
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import java.util.*

class CommentSpecifications private constructor() {
    init {
        throw UnsupportedOperationException()
    }

    companion object {
        fun withAuthorAndPost(authorId: UUID?, postId: UUID?): Specification<Comment> {
            return Specification { root: Root<Comment>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                if (query.resultType == Comment::class.java) {
                    root.fetch<Comment, User>("author", JoinType.LEFT)
                    root.fetch<Comment, Post>("post", JoinType.LEFT)
                }

                withAuthor(authorId).and(withPost(postId)).toPredicate(root, query, criteriaBuilder)
            }
        }

        private fun withAuthor(authorId: UUID?): Specification<Comment> {
            return Specification { root: Root<Comment>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                if (authorId != null) {
                    criteriaBuilder.equal(root.get<Any>("author").get<User>("id"), authorId)
                } else {
                    criteriaBuilder.conjunction()
                }
            }
        }

        private fun withPost(postId: UUID?): Specification<Comment> {
            return Specification { root: Root<Comment>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
                if (postId != null) {
                    criteriaBuilder.equal(root.get<Any>("post").get<Any>("id"), postId)
                } else {
                    criteriaBuilder.conjunction()
                }
            }
        }
    }
}
