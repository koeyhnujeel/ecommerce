package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.shared.Email
import jakarta.persistence.Embeddable

@Embeddable
data class ApplicantInfo(
    val representativeName: String,
    val contractEmail: Email,
    val representativePhone: String,
) {
    init {
        require(representativeName.isNotBlank()) { "대표자 이름은 필수입니다." }
        require(representativePhone.isNotBlank()) { "대표자 번호는 필수입니다." }
    }
}