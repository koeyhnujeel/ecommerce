package com.zunza.ecommerce.application.partner.service

import com.zunza.ecommerce.application.partner.provided.RegisterPartnerUseCase
import com.zunza.ecommerce.application.partner.required.PartnerRepository
import com.zunza.ecommerce.application.partner.service.dto.command.RegisterPartnerCommand
import com.zunza.ecommerce.domain.partner.Partner
import org.springframework.stereotype.Service

@Service
class PartnerCommandService(
    private val partnerRepository: PartnerRepository,
) : RegisterPartnerUseCase {
    override fun registerPartner(command: RegisterPartnerCommand): Long {
        val partner = Partner.register(
            accountId = command.accountId,
            partnerApplicationId = command.partnerApplicationId,
            businessInfo = command.businessInfo,
            settlementAccount = command.settlementAccount
        )

        return partnerRepository.save(partner).id
    }
}