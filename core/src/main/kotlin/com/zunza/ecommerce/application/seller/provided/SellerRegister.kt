package com.zunza.ecommerce.application.seller.provided

import com.zunza.ecommerce.domain.seller.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount

interface SellerRegister {
    fun register(
        accountId: Long,
        sellerApplicationId: Long,
        businessInfo: BusinessInfo,
        settlementAccount: BankAccount,
    ): Long
}