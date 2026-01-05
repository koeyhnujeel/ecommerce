package com.zunza.ecommerce.application.onboarding.required

import com.zunza.ecommerce.domain.onboarding.SellerApplication
import com.zunza.ecommerce.domain.onboarding.SellerApplicationNotFoundException

fun SellerApplicationRepository.findByIdOrThrow(sellerApplicationId: Long): SellerApplication {
    return this.findByIdOrNull(sellerApplicationId)
        ?: throw SellerApplicationNotFoundException()
}

interface SellerApplicationRepository {
    fun existsByBusinessNumber(businessNumber: String): Boolean

    fun save(sellerApplication: SellerApplication): SellerApplication

    fun findByIdOrNull(sellerApplicationId: Long): SellerApplication?

    fun findByAccountId(accountId: Long): SellerApplication?
}