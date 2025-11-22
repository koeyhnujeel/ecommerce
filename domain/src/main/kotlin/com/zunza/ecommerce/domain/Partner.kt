package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.PartnerStatus

class Partner(
    val id: Long,
    val representativeName: String,
    val businessName: String,
    val businessNumber: String,
    val settlementAccountBank: String,
    val settlementAccountNumber: String,
    val phone: String,
    val status: PartnerStatus,
)
