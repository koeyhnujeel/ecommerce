package com.zunza.ecommerce.application.customer.service.dto.command

data class DeleteShippingAddressCommand(
    val accountId: Long,
    val addressId: Long,
)
