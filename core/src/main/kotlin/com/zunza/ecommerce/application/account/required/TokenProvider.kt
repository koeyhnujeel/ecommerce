package com.zunza.ecommerce.application.account.required

import com.zunza.ecommerce.domain.account.UserRole

interface TokenProvider {
    fun generateAccessToken(accountId:Long, roles: List<UserRole>): String

    fun generateRefreshToken(accountId: Long): String

    fun validateToken(token: String)

    fun getAccountId(token: String): Long

    fun getAccountRoles(token: String): List<String>

    fun getRemainingTime(token: String): Long
}