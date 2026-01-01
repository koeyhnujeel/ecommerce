package com.zunza.ecommerce.domain.shared

import jakarta.persistence.Embeddable

@Embeddable
data class Address(
    var roadAddress: String,
    var detailAddress: String,
    var zipcode: String,
) {
    init {
        require(roadAddress.isNotBlank()) { "도로명 주소는 필수입니다." }
        require(zipcode.matches(Regex("\\d{5}"))) { "우편번호 형식이 올바르지 않습니다." }
    }
}
