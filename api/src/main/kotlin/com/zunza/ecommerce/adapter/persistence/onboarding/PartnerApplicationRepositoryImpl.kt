package com.zunza.ecommerce.adapter.persistence.onboarding

import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class PartnerApplicationRepositoryImpl(
    private val partnerApplicationJpaRepository: PartnerApplicationJpaRepository
) : PartnerApplicationRepository {
    override fun existsByBusinessNumber(businessNumber: String): Boolean {
        return partnerApplicationJpaRepository.existsByBusinessInfoBusinessNumber(businessNumber)
    }

    override fun save(partnerApplication: PartnerApplication): PartnerApplication {
        return partnerApplicationJpaRepository.save(partnerApplication)
    }

    override fun findByIdOrNull(partnerApplicationId: Long): PartnerApplication? {
        return partnerApplicationJpaRepository.findByIdOrNull(partnerApplicationId)
    }

    override fun findByAccountId(accountId: Long): PartnerApplication? {
        return partnerApplicationJpaRepository.findByAccountId(accountId)
    }
}