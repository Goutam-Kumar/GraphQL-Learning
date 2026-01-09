package com.droidnext.learning.controller

import com.droidnext.learning.model.Tag
import com.droidnext.learning.repository.TagRepository
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class CreateTagInput(
    val name: String
)

data class UpdateTagInput (
    val id: Long,
    val name: String
)

@Controller
class TagGraphQLController(
    val repository: TagRepository
) {

    @QueryMapping
    suspend fun tags(): List<Tag> = repository.findAll()

    @Transactional
    @MutationMapping
    suspend fun createTag( @Argument input: CreateTagInput): Tag {
        if (repository.existsByName(input.name)) {
            throw IllegalArgumentException("Tag already added")
        }
        return repository.save(Tag(name = input.name))
    }

    @Transactional
    @MutationMapping
    suspend fun updateTag(@Argument input: UpdateTagInput): Tag {
        if (repository.existsByName(input.name)) {
            throw IllegalArgumentException(
                "A tag with name '${input.name}' already exists"
            )
        }
        val tag = repository.findById(input.id)
            .orElseThrow { RuntimeException("Tag not found") }
        val newTag = tag.copy(name = input.name)
        return repository.save(newTag)
    }

    @Transactional
    @MutationMapping
    suspend fun deleteTag(@Argument tagId: Long): Boolean {
        val tag = repository.findById(tagId)
            .orElseThrow { RuntimeException("Tag not found") }
        repository.delete(tag)
        return true
    }
}