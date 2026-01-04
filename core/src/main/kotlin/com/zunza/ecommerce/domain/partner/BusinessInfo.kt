package com.zunza.ecommerce.domain.partner

import jakarta.persistence.Embeddable

@Embeddable
class BusinessInfo(
    val businessNumber: String,
    val companyName: String,
) {
    init {
        require(businessNumber.matches(Regex("\\d{10}"))) { "사업자등록번호 형식이 올바르지 않습니다." }
        require(companyName.isNotBlank()) { "업체명은 필수입니다." }
    }
}