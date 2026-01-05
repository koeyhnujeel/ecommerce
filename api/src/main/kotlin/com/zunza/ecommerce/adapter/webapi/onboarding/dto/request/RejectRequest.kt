package com.zunza.ecommerce.adapter.webapi.onboarding.dto.request

import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand
import jakarta.validation.constraints.NotBlank

data class RejectRequest(
    @field:NotBlank(message = "반려 사유는 필수입니다.")
    val reason: String
) {
    fun toCommand(partnerApplicationId: Long) =
        RejectCommand(partnerApplicationId, reason)
}
