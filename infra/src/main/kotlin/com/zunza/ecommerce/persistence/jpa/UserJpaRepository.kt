package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun existsByEmail(email: String): Boolean
}
