package com.zunza.ecommerce.application.onboarding.service

import com.zunza.ecommerce.application.onboarding.provided.SellerApplicationFinder
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.dto.result.GetSellerApplicationStatusResult
import com.zunza.ecommerce.domain.onboarding.SellerApplicationNotFoundException
import org.springframework.stereotype.Service

@Service
class SellerApplicationQueryService(
    private val sellerApplicationRepository: SellerApplicationRepository,
) : SellerApplicationFinder {
    override fun getSellerApplicationStatus(accountId: Long): GetSellerApplicationStatusResult {
        val partnerApplication = sellerApplicationRepository.findByAccountId(accountId)
            ?: throw SellerApplicationNotFoundException()

        return GetSellerApplicationStatusResult.from(partnerApplication)
    }
}