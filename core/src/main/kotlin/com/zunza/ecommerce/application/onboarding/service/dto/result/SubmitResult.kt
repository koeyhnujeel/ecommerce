package com.zunza.ecommerce.application.onboarding.service.dto.result

data class SubmitResult(
    val partnerApplicationId: Long,
) {
    companion object {
        fun from(partnerApplicationId: Long) =
            SubmitResult(partnerApplicationId)
    }
}
