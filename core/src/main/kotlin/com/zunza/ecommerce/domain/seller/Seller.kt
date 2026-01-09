package com.zunza.ecommerce.domain.seller

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.shared.BankAccount
import jakarta.persistence.Entity

@Entity
class Seller(
    val accountId: Long,
    val sellerApplicationId: Long,
    val businessInfo: BusinessInfo,
    val settlementAccount: BankAccount,
) : AbstractEntity() {
    companion object {
        fun register(
            accountId: Long,
            sellerApplicationId: Long,
            businessInfo: BusinessInfo,
            settlementAccount: BankAccount,
        ) = Seller(
            accountId = accountId,
            sellerApplicationId = sellerApplicationId,
            businessInfo = businessInfo,
            settlementAccount = settlementAccount
        )
    }
}