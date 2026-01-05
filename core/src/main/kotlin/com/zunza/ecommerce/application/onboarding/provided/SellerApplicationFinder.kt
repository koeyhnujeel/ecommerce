package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.service.dto.result.GetSellerApplicationStatusResult

interface SellerApplicationFinder {
    fun getSellerApplicationStatus(accountId: Long): GetSellerApplicationStatusResult
}