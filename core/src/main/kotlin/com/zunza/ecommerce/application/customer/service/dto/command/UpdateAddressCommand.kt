package com.zunza.ecommerce.application.customer.service.dto.command

data class UpdateAddressCommand(
    val accountId: Long,
    val addressId: Long,
    val alias: String,
    val roadAddress: String,
    val detailAddress: String,
    val receiverName: String,
    val zipcode: String,
    val isDefault: Boolean
)
