package com.zunza.ecommerce.domain.onboarding.event

import com.zunza.ecommerce.domain.brand.BrandInfo
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import com.zunza.ecommerce.domain.partner.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount

data class PartnerApplicationApprovedEvent(
    val accountId: Long,
    val partnerApplicationId: Long,
    val businessInfo: BusinessInfo,
    val brandInfo: BrandInfo,
    val settlementAccount: BankAccount
) {
    companion object {
        fun from(partnerApplication: PartnerApplication) =
            PartnerApplicationApprovedEvent(
                partnerApplication.accountId,
                partnerApplication.id,
                partnerApplication.businessInfo,
                partnerApplication.brandInfo,
                partnerApplication.settlementAccount,
            )
    }
}
