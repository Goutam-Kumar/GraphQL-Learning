package com.droidnext.learning.repository

import com.droidnext.learning.model.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository: JpaRepository<Tag, Long> {
    fun existsByName(name: String): Boolean
}