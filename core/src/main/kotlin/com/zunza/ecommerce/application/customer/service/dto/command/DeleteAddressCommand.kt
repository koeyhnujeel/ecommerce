package com.zunza.ecommerce.application.customer.service.dto.command

data class DeleteAddressCommand(
    val accountId: Long,
    val addressId: Long,
)
