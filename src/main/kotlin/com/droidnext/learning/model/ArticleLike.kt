package com.droidnext.learning.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "article_likes",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["article_id", "user_id"])
    ]
)
data class ArticleLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "article_id")
    val article: Article,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

