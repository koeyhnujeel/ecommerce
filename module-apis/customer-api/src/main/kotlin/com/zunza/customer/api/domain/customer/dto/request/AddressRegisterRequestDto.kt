package com.zunza.customer.api.domain.customer.dto.request

import com.zunza.domain.entity.Customer
import com.zunza.domain.entity.CustomerAddress
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AddressRegisterRequestDto(
    @field: NotBlank(message = "배송지 이름을 입력해 주세요.")
    @field: Size(min = 1, max = 10, message ="배송지 이름은 최소 1자 최대 10자로 입력해 주세요.")
    val alias: String,
    @field: NotBlank(message = "우편번호를 입력해 주세요.")
    @field: Size(min = 5, max = 5, message = "우편번호는 5자리입니다.")
    val zipCode: String,
    @field: NotBlank(message = "주소를 입력해 주세요.")
    val address: String,
    @field: NotBlank(message = "상세 주소를 입력해 주세요.")
    val addressDetail: String,
    val isDefault: Boolean
) {
    fun toEntity(customer: Customer) =
        CustomerAddress(
            alias = this.alias,
            zipCode = this.zipCode,
            address = this.address,
            addressDetail = this.addressDetail,
            isDefault = this.isDefault,
            customer = customer
        )
}
