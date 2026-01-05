package com.zunza.ecommerce.domain.partner

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.shared.BankAccount
import jakarta.persistence.Entity

@Entity
class Partner(
    val accountId: Long,
    val partnerApplicationId: Long,
    val businessInfo: BusinessInfo,
    val settlementAccount: BankAccount,
) : AbstractEntity<Partner>() {
    companion object {
        fun register(
            accountId: Long,
            partnerApplicationId: Long,
            businessInfo: BusinessInfo,
            settlementAccount: BankAccount,
        ) = Partner(
            accountId = accountId,
            partnerApplicationId = partnerApplicationId,
            businessInfo = businessInfo,
            settlementAccount = settlementAccount
        )
    }
}