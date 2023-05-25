package io.github.jlmc.firststep.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "t_users")
class User(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column
    val name: String,

    @OneToMany(
        mappedBy = "author",
        orphanRemoval = true,
        cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.REMOVE]
    )
    val posts: Set<Post> = mutableSetOf()
) {
    fun addPosts(posts: Collection<Post>) {
        posts.forEach {
            it.author = this
            (this.posts as MutableSet).add(it)
        }
    }
}
