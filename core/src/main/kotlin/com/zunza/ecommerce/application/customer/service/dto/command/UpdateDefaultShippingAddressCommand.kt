package com.zunza.ecommerce.application.customer.service.dto.command

data class UpdateDefaultShippingAddressCommand(
    val accountId: Long,
    val addressId: Long
)
