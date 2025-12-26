package com.zunza.ecommerce.domain.customer

object CustomerFixture {
    fun createCustomer() = Customer.register(1L, name = "홍길동", phone = "01012345678")
}