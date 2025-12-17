package com.zunza.ecommerce.application.customer.required

import com.zunza.ecommerce.domain.customer.Customer

interface CustomerRepository {
    fun save(customer: Customer): Customer
}