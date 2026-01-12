package com.droidnext.learning.controller

import com.droidnext.learning.model.User
import com.droidnext.learning.repository.UserRepository
import com.droidnext.learning.utils.JwtUtils
import jakarta.transaction.Transactional
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller

data class AuthResponse(
    val user: User,
    val token: String
)

@Controller
class UserGraphQLController(
    private val repository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
) {
    /**
     * Current User details form JWT
     */
    @QueryMapping
    suspend fun me(): User {
        val userId = jwtUtils.currentUserId()
        return  repository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
    }

    // =====================
    // READ ALL USERS
    // =====================
    @QueryMapping
    suspend fun users(): List<User> =
        repository.findAll()

    // =====================
    // CREATE USER
    // =====================
    @Transactional
    @MutationMapping
    suspend fun registerUser(
        @Argument name: String,
        @Argument email: String,
        @Argument password: String
    ): User {
        if (repository.findByEmail(email) != null) {
            throw RuntimeException("Email already exists")
        }

        val user = User(
            name = name,
            email = email,
            password = passwordEncoder.encode(password)
        )
        return repository.save(user)
    }

    /**
     * Login User using email and password and Generate JWT Token
     */
    @MutationMapping
    suspend fun login(
        @Argument email: String,
        @Argument password: String
    ): AuthResponse {
        val user = repository.findByEmail(email)
            ?: throw RuntimeException("Invalid credential")

        if (!passwordEncoder.matches(password, user.password)) {
            throw RuntimeException("Invalid credential")
        }
        val token = jwtUtils.generateWebToken(user)
        return AuthResponse(user , token)
    }

    // =====================
    // UPDATE USER Profile Picture
    // =====================
    @Transactional
    @MutationMapping
    suspend fun updateProfilePicture(
        @Argument profilePictureUrl: String? = null
    ): User {
        val userId = jwtUtils.currentUserId()
        val user = repository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }

        val modifiedUser = user.copy(profilePictureUrl = profilePictureUrl)
        return repository.save(modifiedUser)
    }

    // =====================
    // DELETE USER
    // =====================
    @Transactional
    @MutationMapping
    suspend fun deleteUser(): Boolean {
        val user = repository.findById(jwtUtils.currentUserId())
            .orElseThrow { RuntimeException("User not found") }
        repository.delete(user)
        return true
    }
}