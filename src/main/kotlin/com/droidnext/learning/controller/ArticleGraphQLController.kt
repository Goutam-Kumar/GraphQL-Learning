package com.droidnext.learning.controller

import com.droidnext.learning.model.Article
import com.droidnext.learning.model.ArticleStatus
import com.droidnext.learning.repository.ArticleRepository
import com.droidnext.learning.repository.CategoryRepository
import com.droidnext.learning.repository.TagRepository
import com.droidnext.learning.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class CreateArticleInput(
    val authorId: Long,
    val title: String,
    val content: String,
    val categoryId: String,
    val tagIds: List<String> = emptyList(),
)

@Controller
class ArticleGraphQLController(
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    @QueryMapping
    suspend fun articles(): List<Article> = articleRepository.findAll()


    @Transactional
    @MutationMapping
    suspend fun createArticle(@Argument input: CreateArticleInput): Article {
        val categoryId = input.categoryId.toLong()

        val tagIds = input.tagIds.map { it.toLong() }

        val category = categoryRepository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("Category not found") }

        val tags = tagRepository.findAllById(tagIds).toSet()

        val author = userRepository.findById(input.authorId)
            .orElseThrow { IllegalArgumentException("Author not found") }

        val article = Article(
            title = input.title,
            content = input.content,
            author = author,
            category = category,
            tags = tags
        )

        return articleRepository.save(article)
    }
}