package com.zunza.ecommerce.adapter.persistence.customer

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Repository

@Repository
class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository
) : CustomerRepository {
    override fun save(customer: Customer): Customer {
        return customerJpaRepository.save(customer)
    }

    override fun findWithShippingAddressesByAccountId(accountId: Long): Customer? {
        return customerJpaRepository.findWithShippingAddressesByAccountId(accountId)
    }
}