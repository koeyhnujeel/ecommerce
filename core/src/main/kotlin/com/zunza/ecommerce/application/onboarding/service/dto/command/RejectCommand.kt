package com.zunza.ecommerce.application.onboarding.service.dto.command

data class RejectCommand(
    val sellerApplicationId: Long,
    val reason: String
)
