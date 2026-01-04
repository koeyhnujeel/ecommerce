package com.zunza.ecommerce.application.onboarding.service.dto.command

data class RejectCommand(
    val partnerApplicationId: Long,
    val reason: String
)
