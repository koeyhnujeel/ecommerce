package com.zunza.ecommerce.domain.partner

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.shared.Email
import jakarta.persistence.Entity

@Entity
class Seller(
    val accountId: Long,
    val businessNumber: String,
    val companyName: String,
    val representativeName: String,
    val contractEmail: Email,
    val settlementInfo: SettlementInfo,
) : AbstractEntity()