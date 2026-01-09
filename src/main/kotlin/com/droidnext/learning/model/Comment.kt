package com.droidnext.learning.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(columnDefinition = "TEXT")
    val content: String,

    @ManyToOne
    @JoinColumn(name = "article_id")
    val article: Article,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

