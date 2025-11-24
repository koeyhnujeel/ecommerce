package com.zunza.ecommerce.persistence.impl

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.persistence.entity.CustomerProfileEntity
import com.zunza.ecommerce.persistence.entity.UserEntity
import com.zunza.ecommerce.persistence.jpa.CustomerProfileJpaRepository
import com.zunza.ecommerce.persistence.jpa.UserJpaRepository
import com.zunza.ecommerce.persistence.mapper.toDomain
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import org.springframework.stereotype.Repository

@Repository
class CustomerRepositoryImpl(
    private val customerProfileJpaRepository: CustomerProfileJpaRepository,
    private val userJpaRepository: UserJpaRepository
) : CustomerRepository {

    override fun existsByPhone(phone: String): Boolean {
        return customerProfileJpaRepository.existsByPhone(phone)
    }

    override fun save(customer: Customer): Customer {
        val userEntity = UserEntity(
            email = customer.email,
            password = customer.password,
            userType = customer.userType
        )
        val savedUserEntity = userJpaRepository.save(userEntity)

        val customerProfileEntity = CustomerProfileEntity(
            userEntity = savedUserEntity,
            name = customer.name,
            nickname = customer.nickname,
            phone = customer.phone,
            point = customer.point
        )

        customerProfileJpaRepository.save(customerProfileEntity)
        return customer.copy(id = savedUserEntity.id)
    }

    override fun existsByNickname(nickname: String): Boolean {
        return customerProfileJpaRepository.existsByNickname(nickname)
    }

    override fun findByIdOrThrow(id: Long): Customer {
        val customerEntity = customerProfileJpaRepository.findWithUserById(id)
            ?: throw ErrorCode.CUSTOMER_NOT_FOUND.exception()

        return customerEntity.toDomain()
    }
}
