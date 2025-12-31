package com.zunza.ecommerce.domain.partner

import jakarta.persistence.Embeddable

@Embeddable
data class SettlementInfo(
    val bankCode: String,
    val bankName: String,
    val accountNumber: String,
    val accountHolder: String
)
