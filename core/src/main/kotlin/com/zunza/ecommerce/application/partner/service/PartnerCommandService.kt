package com.zunza.ecommerce.application.partner.service

import com.zunza.ecommerce.application.partner.provided.RegisterPartnerUseCase
import com.zunza.ecommerce.application.partner.required.PartnerRepository
import com.zunza.ecommerce.domain.partner.BusinessInfo
import com.zunza.ecommerce.domain.partner.Partner
import com.zunza.ecommerce.domain.shared.BankAccount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PartnerCommandService(
    private val partnerRepository: PartnerRepository,
) : RegisterPartnerUseCase {
    override fun registerPartner(
        accountId: Long,
        partnerApplicationId: Long,
        businessInfo: BusinessInfo,
        settlementAccount: BankAccount
    ): Long {
        val partner = Partner.register(
            accountId = accountId,
            partnerApplicationId = partnerApplicationId,
            businessInfo = businessInfo,
            settlementAccount = settlementAccount
        )

        return partnerRepository.save(partner).id
    }
}