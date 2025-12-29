package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.domain.account.Account

interface GetCustomerAccountUseCase {
    fun findByEmail(email: String): Account?

    fun findByIdOrThrow(accountId: Long): Account
}