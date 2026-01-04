package com.zunza.ecommerce.adapter.persistence.customer

import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CustomerJpaRepository : JpaRepository<Customer, Long> {
    fun findByAccountId(accountId: Long): Customer?

    @Query(
        """
            SELECT c 
            FROM Customer c
            LEFT JOIN FETCH c.shippingAddresses
            WHERE c.accountId = :accountId
        """
    )
    fun findWithShippingAddressesByAccountId(@Param("accountId") accountId: Long): Customer?
}