package com.droidnext.learning.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    @Column(unique = true, nullable = false)
    val email: String = "",
    @Column(nullable = false)
    val password: String,
    val profilePictureUrl: String? = null
)