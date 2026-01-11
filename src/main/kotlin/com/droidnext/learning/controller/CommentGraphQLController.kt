package com.droidnext.learning.controller

import com.droidnext.learning.model.Comment
import com.droidnext.learning.repository.ArticleRepository
import com.droidnext.learning.repository.CommentRepository
import com.droidnext.learning.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class AddCommentInput(
    val articleId: String,
    val userId: String,
    val content: String
)

data class EditCommentInput(
    val userId: String,
    val commentId: String,
    val content: String
)

@Controller
class CommentGraphQLController(
    val commentRepository: CommentRepository,
    val articleRepository: ArticleRepository,
    val userRepository: UserRepository
) {
    @QueryMapping
    suspend fun comments(@Argument articleId: String): List<Comment> {
        val article = articleRepository.findById(articleId.toLong())
            .orElseThrow { IllegalArgumentException("Article not found!") }

        return commentRepository.findByArticleId(article.id)
    }

    @Transactional
    @MutationMapping
    suspend fun addComment(
        @Argument input: AddCommentInput
    ): Comment {
        val user = userRepository.findById(input.userId.toLong())
            .orElseThrow { IllegalArgumentException("User not found!") }

        val article = articleRepository.findById(input.articleId.toLong())
            .orElseThrow { IllegalArgumentException("Article not found!") }

        val comment = Comment (
            content = input.content,
            article = article,
            user = user
        )
        return commentRepository.save(comment)
    }

    @Transactional
    @MutationMapping
    suspend fun editComment(
        @Argument input: EditCommentInput
    ): Comment {
        val user = userRepository.findById(input.userId.toLong())
            .orElseThrow { IllegalArgumentException("User not found!") }

        val comment = commentRepository.findById(input.commentId.toLong())
            .orElseThrow { IllegalArgumentException("Comment not found") }

        if (user.id != comment.user.id)
            throw RuntimeException("You are not authorized to update comment")

        val newComment = comment.copy(content = input.content)
        return commentRepository.save(newComment)
    }

    @Transactional
    @MutationMapping
    suspend fun deleteComment(
        @Argument userId: String,
        @Argument commentId: String
    ): Boolean {
        val user = userRepository.findById(userId.toLong())
            .orElseThrow { IllegalArgumentException("User not found!") }

        val  comment = commentRepository.findById(commentId.toLong())
            .orElseThrow { IllegalArgumentException("Comment not found") }

        if (user.id != comment.user.id)
            throw RuntimeException("You are not the owner of this comment")

        commentRepository.delete(comment)
        return true
    }
}