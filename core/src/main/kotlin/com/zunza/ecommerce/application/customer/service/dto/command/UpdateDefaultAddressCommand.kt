package com.zunza.ecommerce.application.customer.service.dto.command

data class UpdateDefaultAddressCommand(
    val accountId: Long,
    val addressId: Long
)
