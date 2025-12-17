package com.zunza.ecommerce.domain.account

import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    val address: String
) {
    init {
        require(address.isNotBlank()) { "이메일은 빈 값일 수 없습니다." }
        require(REGEX.matches(address)) { "잘못된 이메일 형식입니다." }
    }

    companion object {
        private val REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}