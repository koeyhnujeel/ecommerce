package com.zunza.ecommerce.domain.shared

import jakarta.persistence.Embeddable

@Embeddable
data class BankAccount(
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String
) {
    init {
        require(bankName.isNotBlank()) { "은행은 필수입니다." }
        require(accountNumber.isNotBlank()) { "계좌번호는 필수입니다." }
        require(accountHolder.isNotBlank()) { "예금주명은 필수입니다." }
    }
}
