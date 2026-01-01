package com.droidnext.learning.controller

import com.droidnext.learning.model.User
import com.droidnext.learning.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserGraphQLController(
    private val repository: UserRepository
) {
    @QueryMapping
    suspend fun users(): List<User> =
        repository.findAll()

    @Transactional
    @MutationMapping
    suspend fun createUser(
        @Argument name: String,
        @Argument email: String
    ): User = repository.save(User(name = name, email = email))

    @Transactional
    @MutationMapping
    suspend fun updateProfilePicture(
        @Argument userId: Long,
        @Argument profilePictureUrl: String? = null
    ): User {
        val user = repository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        val modifiedUser = user.copy(profilePictureUrl = profilePictureUrl)
        return repository.save(modifiedUser)
    }
}