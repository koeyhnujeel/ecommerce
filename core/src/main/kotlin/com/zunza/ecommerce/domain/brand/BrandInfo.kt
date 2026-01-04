package com.zunza.ecommerce.domain.brand

import jakarta.persistence.Embeddable

@Embeddable
data class BrandInfo(
    val nameKor: String,
    val nameEng: String,
    val description: String,
) {
    init {
        require(nameKor.isNotBlank()) { "브랜드 이름(국문)은 필수입니다." }
        require(nameEng.isNotBlank()) { "브랜드 이름(영문)은 필수입니다." }
        require(description.isNotBlank()) { "브랜드 소개는 필수입니다." }
    }
}