package com.zunza.ecommerce.application.auth.required


interface TokenRepository {
    fun save(accountId: Long, token: String)

    fun findById(accountId: Long): String?
}