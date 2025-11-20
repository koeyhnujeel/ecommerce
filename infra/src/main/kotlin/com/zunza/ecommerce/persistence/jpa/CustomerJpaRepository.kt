package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.CustomerEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerJpaRepository : JpaRepository<CustomerEntity, Long> {
    fun existsByNickname(nickname: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun existsByPhone(phone: String): Boolean

    fun findByEmail(email: String): CustomerEntity?
}
