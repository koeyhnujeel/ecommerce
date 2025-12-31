package com.zunza.ecommerce.application.account.required


interface TokenRepository {
    fun save(accountId: Long, token: String)

    fun findById(accountId: Long): String?

    fun addBlacklist(token: String, remainingTime: Long)

    fun removeToken(accountId: Long)

    fun isBlacklisted(token: String): Boolean
}