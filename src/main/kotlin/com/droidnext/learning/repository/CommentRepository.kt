package com.droidnext.learning.repository

import com.droidnext.learning.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long> {
    fun findByArticleId(articleId: Long): List<Comment>
}