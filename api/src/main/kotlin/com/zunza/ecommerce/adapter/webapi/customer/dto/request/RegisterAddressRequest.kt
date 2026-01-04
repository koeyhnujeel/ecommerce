package com.zunza.ecommerce.adapter.webapi.customer.dto.request

import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand
import jakarta.validation.constraints.NotBlank

data class RegisterAddressRequest(
    @field:NotBlank(message = "배송지 별칭을 입력해 주세요.")
    val alias: String,
    @field:NotBlank(message = "주소를 입력해 주세요.")
    val roadAddress: String,
    val detailAddress: String,
    @field:NotBlank(message = "수령인을 입력해 주세요.")
    val receiverName: String,
    @field:NotBlank(message = "우편번호를 입력해 주세요.")
    val zipcode: String,
    val isDefault: Boolean
) {
    fun toCommand(accountId: Long) =
        RegisterShippingAddressCommand(
            accountId = accountId,
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = isDefault
        )
}
