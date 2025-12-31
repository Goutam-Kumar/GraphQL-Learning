package com.droidnext.learning

import jakarta.annotation.PostConstruct
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class DbConnectionChecker(private val jdbcTemplate: JdbcTemplate) {
    @PostConstruct
    fun checkConnection() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
            println("✅ Database connection successful")
        } catch (ex: Exception) {
            throw IllegalStateException(
                "❌ Database connection failed at startup",
                ex
            )
        }
    }
}