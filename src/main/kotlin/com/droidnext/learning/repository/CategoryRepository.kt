package com.droidnext.learning.repository

import com.droidnext.learning.model.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository: JpaRepository<Category, Long> {
    fun existsByName(name: String): Boolean
}