package com.droidnext.learning.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,

    @Column(columnDefinition = "TEXT")
    val content: String,

    @Enumerated(EnumType.STRING)
    val status: ArticleStatus = ArticleStatus.DRAFT,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    val author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,

    @ManyToMany
    @JoinTable(
        name = "article_tags",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    val tags: Set<Tag> = emptySet(),

    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ArticleStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}
