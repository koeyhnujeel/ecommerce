package com.zunza.ecommerce.application.customer.service.dto.command

data class RegisterAddressCommand(
    val accountId: Long,
    val alias: String,
    val roadAddress: String,
    val detailAddress: String,
    val receiverName: String,
    val zipcode: String,
    val isDefault: Boolean
)
