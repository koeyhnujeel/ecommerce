package com.zunza.ecommerce.application.auth.required

interface TokenProvider {
    fun generateAccessToken(accountId:Long, role: String): String

    fun generateRefreshToken(accountId: Long): String

    fun validateToken(token: String)

    fun getAccountId(token: String): Long

    fun getAccountRole(token: String): String
}