package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.PartnerStatus
import com.zunza.ecommerce.domain.enums.UserType
import java.time.LocalDateTime

data class Partner(
    val id: Long,
    val email: String,
    val password: String,
    val representativeName: String,
    val businessName: String,
    val businessNumber: String,
    val settlementAccountBank: String,
    val settlementAccountNumber: String,
    val phone: String,
    val status: PartnerStatus,
    val userType: UserType,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
