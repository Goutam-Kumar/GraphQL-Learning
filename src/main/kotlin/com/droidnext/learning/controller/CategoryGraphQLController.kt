package com.droidnext.learning.controller

import com.droidnext.learning.model.Category
import com.droidnext.learning.repository.CategoryRepository
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class CreateCategoryInput (
    val name: String
)

data class UpdateCategoryInput(
    val id: Long,
    val name: String
)

@Controller
class CategoryGraphQLController(
    private val categoryRepository: CategoryRepository
) {

    @QueryMapping
    suspend fun categories(): List<Category> =
        categoryRepository.findAll()

    @Transactional
    @MutationMapping
    suspend fun createCategory(@Argument input: CreateCategoryInput): Category {
        if (categoryRepository.existsByName(input.name)) {
            throw IllegalArgumentException("Category already exists")
        }

        val category = Category(name = input.name)
        return categoryRepository.save(category)
    }

    @Transactional
    @MutationMapping
    suspend fun updateCategory(@Argument input: UpdateCategoryInput): Category {
        val category = categoryRepository.findById(input.id)
            .orElseThrow { RuntimeException("Category not found") }

        val modifiedCategory = category.copy(name = input.name)
        return categoryRepository.save(modifiedCategory)
    }

    @Transactional
    @MutationMapping
    suspend fun deleteCategory(@Argument categoryId: Long): Boolean {
        val category = categoryRepository.findById(categoryId)
            .orElseThrow { RuntimeException("Category not found!") }

        categoryRepository.delete(category)
        return true
    }
}