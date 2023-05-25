package io.github.jlmc.firststep.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@Entity
@Table(name = "t_posts")
@EntityListeners(AuditingEntityListener::class)
class Post(

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column
    val title: String,

    @Column
    val description: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id", nullable = false, updatable = false)
    var author: User,

    @Column(name = "votes", nullable = false, updatable = true)
    var votes: Int = 0,

    @CreatedDate
    //@CreationTimestamp
    @Column(name ="created_date")
    var createdDate: Instant? = null,
    @LastModifiedDate
    @Column(name = "last_modified_date")
    var lastModifiedDate: Instant? = null,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "system", column = Column(name = "src_system", updatable = false))
    )
    val srcSystem: SystemAware? = SystemAware.createSystemAware("D"),
)

