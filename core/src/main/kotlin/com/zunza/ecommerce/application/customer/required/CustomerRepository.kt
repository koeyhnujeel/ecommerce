package com.zunza.ecommerce.application.customer.required

import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException

fun CustomerRepository.findWithAddressesOrThrow(accountId: Long): Customer {
    return this.findWithAddressesByAccountId(accountId)
        ?: throw CustomerNotFoundException()
}

interface CustomerRepository {
    fun save(customer: Customer): Customer

    fun findWithAddressesByAccountId(accountId: Long): Customer?
}