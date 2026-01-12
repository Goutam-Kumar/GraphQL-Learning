package com.droidnext.learning.controller

import com.droidnext.learning.model.Article
import com.droidnext.learning.model.ArticleStatus
import com.droidnext.learning.repository.ArticleRepository
import com.droidnext.learning.repository.CategoryRepository
import com.droidnext.learning.repository.TagRepository
import com.droidnext.learning.repository.UserRepository
import com.droidnext.learning.utils.JwtUtils
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class CreateArticleInput(
    val title: String,
    val content: String,
    val categoryId: String,
    val tagIds: List<String> = emptyList()
)

@Controller
class ArticleGraphQLController(
    private val jwtUtils: JwtUtils,
    private val articleRepository: ArticleRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository
) {
    /**
     * Get All articles
     */
    @QueryMapping
    suspend fun articles(): List<Article> {
        userRepository.findById(jwtUtils.currentUserId())
            .orElseThrow { RuntimeException("Unauthorized") }

        return articleRepository.findAll()
    }

    /**
     * Get All Articles posted by a specific user
     */
    @QueryMapping
    suspend fun articlesPostedByUser(): List<Article> {
        val uId = jwtUtils.currentUserId()
        val user = userRepository.findById(uId)
            .orElseThrow { IllegalArgumentException("User not found!") }

        return articleRepository.findByAuthorId(user.id)
    }

    /**
     * Get Article details
     */
    @QueryMapping
    suspend fun articleById(
        @Argument id: String
    ): Article {
        userRepository.findById(jwtUtils.currentUserId())
            .orElseThrow { RuntimeException("Unauthorized") }

        val artId = id.toLong()
        val article = articleRepository.findById(artId)
            .orElseThrow { IllegalArgumentException("Article not found!") }
        return article
    }

    /**
     * Get all articles by Category
     */
    @QueryMapping
    suspend fun articlesByCategory(
        @Argument categoryId: String
    ): List<Article> {
        userRepository.findById(jwtUtils.currentUserId())
            .orElseThrow { RuntimeException("Unauthorized") }

        val category = categoryRepository.findById(categoryId.toLong())
            .orElseThrow { IllegalArgumentException("Category not found") }
        return articleRepository.findByCategoryId(category.id)
    }

    /**
     * Create Article
     */
    @Transactional
    @MutationMapping
    suspend fun createArticle(@Argument input: CreateArticleInput): Article {
        val author = userRepository.findById(jwtUtils.currentUserId())
            .orElseThrow { IllegalArgumentException("Author not found") }

        val categoryId = input.categoryId.toLong()

        val tagIds = input.tagIds.map { it.toLong() }

        val category = categoryRepository.findById(categoryId)
            .orElseThrow { IllegalArgumentException("Category not found") }

        val tags = tagRepository.findAllById(tagIds).toSet()

        val article = Article(
            title = input.title,
            content = input.content,
            author = author,
            category = category,
            tags = tags
        )

        return articleRepository.save(article)
    }

    /**
     * Update Article Status
     */
    @Transactional
    @MutationMapping
    suspend fun changeArticleStatus(
        @Argument articleId: String,
        @Argument status: ArticleStatus
    ): Article {
        val author = userRepository.findById(jwtUtils.currentUserId())
            .orElseThrow { RuntimeException("Unauthorized") }

        val id = articleId.toLong()
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Article not found!") }

        if (author.id != article.author.id) {
            throw RuntimeException("You are not authorized to change the status")
        }

        val newArticle = article.copy(status = status)
        return articleRepository.save(newArticle)
    }

    /**
     * Update Article Category
     */
    @Transactional
    @MutationMapping
    suspend fun updateArticleCategory(
        @Argument articleId: String,
        @Argument categoryId: String
    ): Article {
        val id = articleId.toLong()
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Article not found!") }

        val catId = categoryId.toLong()
        val category = categoryRepository.findById(catId)
            .orElseThrow { IllegalArgumentException("Category not found!") }

        val newArticle = article.copy(
            category = category
        )
        return articleRepository.save(newArticle)
    }

    /**
     * Update Article Tags
     */
    @Transactional
    @MutationMapping
    suspend fun updateArticleTag(
        @Argument articleId: String,
        @Argument tagIds: List<String>
    ): Article {
        val id = articleId.toLong()
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Article not found!") }

        val newTags = tagIds.map { it.toLong() }
        val tags = tagRepository.findAllById(newTags).toSet()
        val newArticle = article.copy(
            tags = tags
        )
        return articleRepository.save(newArticle)
    }

    /**
     * Update Article Content
     */
    @Transactional
    @MutationMapping
    suspend fun updateArticleContent(
        @Argument articleId: String,
        @Argument content: String
    ): Article {
        val id = articleId.toLong()
        val article = articleRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Article not found!") }

        val newArticle = article.copy(
            content = content
        )
        return articleRepository.save(newArticle)
    }

    /**
     * Delete Article
     */
    @Transactional
    @MutationMapping
    suspend fun deleteArticle(
        @Argument articleId: String
    ): Boolean {
        val artId = articleId.toLong()
        val article = articleRepository.findById(artId)
            .orElseThrow { IllegalArgumentException("Article not found!") }
        articleRepository.delete(article)
        return true
    }
}