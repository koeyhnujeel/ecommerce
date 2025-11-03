package com.zunza.domain.repository

import com.zunza.domain.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun existsByEmail(email: String): Boolean

    fun existsByPhone(phone: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun findByEmail(email: String): Customer?
}
