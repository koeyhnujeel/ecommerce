package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountFinder
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.Email
import org.springframework.stereotype.Service

@Service
class AccountFinderService(
    private val accountRepository: AccountRepository
) : AccountFinder {
    override fun findByEmail(email: String): Account? {
        return accountRepository.findByEmail(Email(email))
    }

    override fun findByIdOrThrow(accountId: Long): Account {
        return accountRepository.findByIdOrNull(accountId)
            ?: throw AccountNotFoundException()
    }
}