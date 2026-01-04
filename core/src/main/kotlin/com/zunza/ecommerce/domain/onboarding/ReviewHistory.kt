package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class ReviewHistory(
    val result: ReviewResult,
    val comment: String,
    val reviewedAt: LocalDateTime,
) : AbstractEntity() {
    companion object {
        fun approve() = ReviewHistory(
            result = ReviewResult.APPROVED,
            comment = "입점이 승인됐습니다.",
            reviewedAt = LocalDateTime.now()
        )

        fun reject(reason: String) = ReviewHistory(
            result = ReviewResult.REJECTED,
            comment = reason,
            reviewedAt = LocalDateTime.now()
        )
    }
}