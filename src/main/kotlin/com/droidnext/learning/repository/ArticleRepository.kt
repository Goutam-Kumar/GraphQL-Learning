package com.droidnext.learning.repository

import com.droidnext.learning.model.Article
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository: JpaRepository<Article, Long> {
    fun findByAuthorId(authorId: Long): List<Article>
    fun findByCategoryId(categoryId: Long): List<Article>
}