package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.persistence.jpa.UserJpaRepository
import com.zunza.ecommerce.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }
}
