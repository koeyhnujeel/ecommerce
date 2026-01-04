package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.service.dto.result.CheckApplicationStatusResult

interface CheckApplicationStatusUseCase {
    fun checkApplicationStatus(accountId: Long): CheckApplicationStatusResult
}