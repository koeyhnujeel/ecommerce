package com.zunza.ecommerce.repository

interface UserRepository {
    fun existsByEmail(email: String): Boolean
}
