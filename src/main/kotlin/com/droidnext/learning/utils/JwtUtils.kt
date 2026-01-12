package com.droidnext.learning.utils

import com.droidnext.learning.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {

    @Value("\${jwt.secret}")
    private lateinit var secret: String
    private val expirationMs = 1000 * 60 * 60

    fun generateWebToken(user: User): String {
        return Jwts.builder()
            .setSubject(user.email)
            .claim("userId", user.id)
            .claim("name", user.name)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(Keys.hmacShaKeyFor(secret.toByteArray()))
            .compact()
    }

    fun extractUserId(token: String): Long =
        Jwts.parserBuilder()
            .setSigningKey(secret.toByteArray())
            .build()
            .parseClaimsJws(token)
            .body["userId"].toString().toLong()

    // =====================
    // HELPER: GET USER ID FROM JWT
    // =====================
    fun currentUserId(): Long {
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw RuntimeException("Unauthorized")

        if (!auth.isAuthenticated || auth.principal !is Long) {
            throw RuntimeException("Unauthorized")
        }

        return auth.principal as Long
    }
}