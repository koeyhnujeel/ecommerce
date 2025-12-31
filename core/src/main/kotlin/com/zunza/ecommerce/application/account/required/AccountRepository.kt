package com.zunza.ecommerce.application.account.required

import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.shared.Email

fun AccountRepository.findByIdOrThrow(accountId: Long): Account {
    return this.findByIdOrNull(accountId)
        ?: throw AccountNotFoundException()
}

interface AccountRepository {
    fun save(account: Account): Account

    fun existsByEmail(email: Email): Boolean

    fun findByIdOrNull(accountId: Long): Account?

    fun findByEmail(email: Email): Account?
}