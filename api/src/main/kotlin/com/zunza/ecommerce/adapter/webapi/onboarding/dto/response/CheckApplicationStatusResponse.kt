package com.zunza.ecommerce.adapter.webapi.onboarding.dto.response

import com.zunza.ecommerce.application.onboarding.service.dto.result.CheckApplicationStatusResult
import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import java.time.LocalDateTime

data class CheckApplicationStatusResponse(
    val applicationId: Long,
    val representativeName: String,
    val status: ApplicationStatus,
    val submittedAt: LocalDateTime
) {
    companion object {
        fun from(result: CheckApplicationStatusResult) =
            CheckApplicationStatusResponse(
                result.applicationId,
                result.representativeName,
                result.status,
                result.submittedAt
            )
    }
}