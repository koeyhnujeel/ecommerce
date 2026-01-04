package com.zunza.ecommerce.application.onboarding.service.dto.result

import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import java.time.LocalDateTime

data class CheckApplicationStatusResult(
    val applicationId: Long,
    val representativeName: String,
    val status: ApplicationStatus,
    val submittedAt: LocalDateTime,
) {
    companion object {
        fun from(partnerApplication: PartnerApplication) =
            CheckApplicationStatusResult(
            applicationId = partnerApplication.id,
            representativeName = partnerApplication.applicantInfo.representativeName,
            status = partnerApplication.status,
            submittedAt = partnerApplication.submittedAt,
        )
    }
}
