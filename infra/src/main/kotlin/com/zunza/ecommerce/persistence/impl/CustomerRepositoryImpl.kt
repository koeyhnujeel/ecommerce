package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.persistence.jpa.CustomerJpaRepository
import com.zunza.ecommerce.persistence.mapper.toDomain
import com.zunza.ecommerce.persistence.mapper.toEntity
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import org.springframework.stereotype.Repository

@Repository
class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {

    override fun existsByEmail(email: String): Boolean {
        return customerJpaRepository.existsByEmail(email)
    }

    override fun existsByPhone(phone: String): Boolean {
        return customerJpaRepository.existsByPhone(phone)
    }

    override fun save(customer: Customer): Customer {
        val customerEntity = customerJpaRepository.save(customer.toEntity())
        return customerEntity.toDomain()
    }

    override fun existsByNickname(nickname: String): Boolean {
        return customerJpaRepository.existsByNickname(nickname)
    }

    override fun findByEmailOrThrow(email: String): Customer {
        val customerEntity = customerJpaRepository.findByEmail(email)
            ?: throw ErrorCode.CUSTOMER_NOT_FOUND.exception()

        return customerEntity.toDomain()
    }
}
