package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand

object RejectCommandFixture {

    fun createRejectCommand(
        sellerApplicationId: Long = 1L,
        reason: String = "Test reason"
    ) = RejectCommand(
        sellerApplicationId = sellerApplicationId,
        reason = reason
    )
}
