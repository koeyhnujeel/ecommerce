package com.zunza.ecommerce.application.customer.service.dto.command


data class CustomerRegisterCommand(
    val accountId: Long,
    val name: String,
    val phone: String
) {
    init {
        require(accountId > 0)
        require(name.length in 2..15)
        require(phone.length == 11)
    }
}
