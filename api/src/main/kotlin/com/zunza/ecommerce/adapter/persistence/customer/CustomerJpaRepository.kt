package com.zunza.ecommerce.adapter.persistence.customer

import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerJpaRepository : JpaRepository<Customer, Long> {
    fun findByAccountId(accountId: Long): Customer?

    @EntityGraph(attributePaths = ["shippingAddresses"])
    fun findWithAddressesByAccountId(accountId: Long): Customer?
}