package com.zunza.ecommerce.adapter.persistence.onboarding

import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class SellerApplicationRepositoryImpl(
    private val sellerApplicationJpaRepository: SellerApplicationJpaRepository
) : SellerApplicationRepository {
    override fun existsByBusinessNumber(businessNumber: String): Boolean {
        return sellerApplicationJpaRepository.existsByBusinessInfoBusinessNumber(businessNumber)
    }

    override fun save(sellerApplication: SellerApplication): SellerApplication {
        return sellerApplicationJpaRepository.save(sellerApplication)
    }

    override fun findByIdOrNull(sellerApplicationId: Long): SellerApplication? {
        return sellerApplicationJpaRepository.findByIdOrNull(sellerApplicationId)
    }

    override fun findByAccountId(accountId: Long): SellerApplication? {
        return sellerApplicationJpaRepository.findByAccountId(accountId)
    }
}