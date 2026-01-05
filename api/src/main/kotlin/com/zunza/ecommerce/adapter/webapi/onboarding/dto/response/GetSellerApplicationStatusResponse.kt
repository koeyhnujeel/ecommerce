package com.zunza.ecommerce.adapter.webapi.onboarding.dto.response

import com.zunza.ecommerce.application.onboarding.service.dto.result.GetSellerApplicationStatusResult
import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import java.time.LocalDateTime

data class GetSellerApplicationStatusResponse(
    val sellerApplicationId: Long,
    val representativeName: String,
    val status: ApplicationStatus,
    val submittedAt: LocalDateTime
) {
    companion object {
        fun from(result: GetSellerApplicationStatusResult) =
            GetSellerApplicationStatusResponse(
                result.sellerApplicationId,
                result.representativeName,
                result.status,
                result.submittedAt
            )
    }
}