package com.zunza.ecommerce.application.onboarding.service

import com.zunza.ecommerce.application.onboarding.provided.CheckApplicationStatusUseCase
import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.dto.result.CheckApplicationStatusResult
import com.zunza.ecommerce.domain.onboarding.PartnerApplicationNotFoundException
import org.springframework.stereotype.Service

@Service
class PartnerApplicationQueryService(
    private val partnerApplicationRepository: PartnerApplicationRepository,
) : CheckApplicationStatusUseCase {
    override fun checkApplicationStatus(accountId: Long): CheckApplicationStatusResult {
        val partnerApplication = partnerApplicationRepository.findByAccountId(accountId)
            ?: throw PartnerApplicationNotFoundException()

        return CheckApplicationStatusResult.from(partnerApplication)
    }
}