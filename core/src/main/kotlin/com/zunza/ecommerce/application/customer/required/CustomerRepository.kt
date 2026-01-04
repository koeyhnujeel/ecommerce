package com.zunza.ecommerce.application.customer.required

import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException

fun CustomerRepository.findWithShippingAddressesOrThrow(accountId: Long): Customer {
    return this.findWithShippingAddressesByAccountId(accountId)
        ?: throw CustomerNotFoundException()
}

interface CustomerRepository {
    fun save(customer: Customer): Customer

    fun findWithShippingAddressesByAccountId(accountId: Long): Customer?
}