package com.zunza.ecommerce.application.onboarding.required

import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import com.zunza.ecommerce.domain.onboarding.PartnerApplicationNotFoundException

fun PartnerApplicationRepository.findByIdOrThrow(partnerApplicationId: Long): PartnerApplication {
    return this.findByIdOrNull(partnerApplicationId)
        ?: throw PartnerApplicationNotFoundException()
}

interface PartnerApplicationRepository {
    fun existsByBusinessNumber(businessNumber: String): Boolean

    fun save(partnerApplication: PartnerApplication): PartnerApplication

    fun findByIdOrNull(partnerApplicationId: Long): PartnerApplication?

    fun findByAccountId(accountId: Long): PartnerApplication?
}