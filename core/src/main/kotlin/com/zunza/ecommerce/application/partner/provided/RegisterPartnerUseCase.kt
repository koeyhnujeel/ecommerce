package com.zunza.ecommerce.application.partner.provided

import com.zunza.ecommerce.domain.partner.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount

interface RegisterPartnerUseCase {
    fun registerPartner(
        accountId: Long,
        partnerApplicationId: Long,
        businessInfo: BusinessInfo,
        settlementAccount: BankAccount,
    ): Long
}