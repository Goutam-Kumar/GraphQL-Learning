package com.droidnext.learning.model

import jakarta.persistence.*

@Entity
@Table(name = "categories")
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val name: String,

    @OneToMany(mappedBy = "category")
    val articles: List<Article> = emptyList()
)

