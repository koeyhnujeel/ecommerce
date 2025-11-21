package com.zunza.ecommerce.repository

import com.zunza.ecommerce.domain.User

interface UserRepository {
    fun existsByEmail(email: String): Boolean

    fun findByEmailOrNull(email: String): User?
}
