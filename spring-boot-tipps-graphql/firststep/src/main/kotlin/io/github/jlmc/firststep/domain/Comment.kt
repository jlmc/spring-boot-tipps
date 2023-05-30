package io.github.jlmc.firststep.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "t_comments")
class Comment(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @field:Column
    var text: String,
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false, updatable = false)
    var post: Post,
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    var author: User
)
