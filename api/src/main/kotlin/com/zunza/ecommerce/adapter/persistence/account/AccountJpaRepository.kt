package com.zunza.ecommerce.adapter.persistence.account

import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.shared.Email
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<Account, Long> {
    fun existsByEmail(email: Email): Boolean

    fun findByEmail(email: Email): Account?
}