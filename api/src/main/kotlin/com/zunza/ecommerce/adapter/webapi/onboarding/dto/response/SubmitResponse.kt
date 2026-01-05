package com.zunza.ecommerce.adapter.webapi.onboarding.dto.response

data class SubmitResponse(
    val partnerApplicationId: Long
) {
    companion object {
        fun from(partnerApplicationId: Long) =
            SubmitResponse(partnerApplicationId)
    }
}
