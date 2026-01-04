package com.zunza.ecommerce.application.partner.service.dto.command

import com.zunza.ecommerce.domain.onboarding.event.PartnerApplicationApprovedEvent
import com.zunza.ecommerce.domain.partner.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount

data class RegisterPartnerCommand(
    val accountId: Long,
    val partnerApplicationId: Long,
    val businessInfo: BusinessInfo,
    val settlementAccount: BankAccount
) {
    companion object {
        fun from(event: PartnerApplicationApprovedEvent) =
            RegisterPartnerCommand(
                event.accountId,
                event.partnerApplicationId,
                event.businessInfo,
                event.settlementAccount
            )
    }
}
