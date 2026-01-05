package com.zunza.ecommerce.application.onboarding.service.dto.result

import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import java.time.LocalDateTime

data class GetSellerApplicationStatusResult(
    val sellerApplicationId: Long,
    val representativeName: String,
    val status: ApplicationStatus,
    val submittedAt: LocalDateTime,
) {
    companion object {
        fun from(sellerApplication: SellerApplication) =
            GetSellerApplicationStatusResult(
            sellerApplicationId = sellerApplication.id,
            representativeName = sellerApplication.applicantInfo.representativeName,
            status = sellerApplication.status,
            submittedAt = sellerApplication.submittedAt,
        )
    }
}
