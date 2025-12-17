package com.zunza.ecommerce.adapter.persistence.account

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.Email
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryImpl(
    private val accountJpaRepository: AccountJpaRepository
) : AccountRepository {
    override fun save(account: Account): Account {
        return accountJpaRepository.save(account)
    }

    override fun existsByEmail(email: Email): Boolean {
        return accountJpaRepository.existsByEmail(email)
    }

    override fun findByIdOrNull(accountId: Long): Account? {
        return accountJpaRepository.findByIdOrNull(accountId)
    }
}