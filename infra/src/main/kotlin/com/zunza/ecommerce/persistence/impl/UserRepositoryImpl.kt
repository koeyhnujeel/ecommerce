package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.domain.User
import com.zunza.ecommerce.persistence.jpa.UserJpaRepository
import com.zunza.ecommerce.persistence.mapper.toDomain
import com.zunza.ecommerce.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {

    override fun existsByEmail(email: String): Boolean {
        return userJpaRepository.existsByEmail(email)
    }

    override fun findByEmailOrNull(email: String): User? {
        val userEntity = userJpaRepository.findByEmail(email)
        return userEntity?.toDomain()
    }
}
