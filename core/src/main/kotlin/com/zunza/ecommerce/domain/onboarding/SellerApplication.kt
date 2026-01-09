package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.brand.BrandInfo
import com.zunza.ecommerce.domain.seller.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount
import com.zunza.ecommerce.domain.shared.Email
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class SellerApplication private constructor(
    val accountId: Long,
    val applicantInfo: ApplicantInfo,
    val businessInfo: BusinessInfo,
    val brandInfo: BrandInfo,
    val settlementAccount: BankAccount,
    var status: ApplicationStatus,
    val submittedAt: LocalDateTime,
    val reviewHistories: MutableList<ReviewHistory> = mutableListOf()
) : AbstractEntity() {
    companion object {
        fun submit(
            accountId: Long,
            representativeName: String,
            contactEmail: String,
            representativePhone: String,
            businessNumber: String,
            companyName: String,
            brandNameKor: String,
            brandNameEng: String,
            brandDescription: String,
            bankName: String,
            accountNumber: String,
            accountHolder: String
        ): SellerApplication {
            val applicantInfo = ApplicantInfo(representativeName, Email(contactEmail), representativePhone)
            val businessInfo = BusinessInfo(businessNumber, companyName)
            val brandInfo = BrandInfo(brandNameKor, brandNameEng, brandDescription)
            val bankAccount = BankAccount(bankName, accountNumber, accountHolder)

            return SellerApplication(
                accountId = accountId,
                applicantInfo = applicantInfo,
                businessInfo = businessInfo,
                brandInfo = brandInfo,
                settlementAccount = bankAccount,
                status = ApplicationStatus.SUBMITTED,
                submittedAt = LocalDateTime.now(),
            )
        }
    }

    fun startReview() {
        check(status == ApplicationStatus.SUBMITTED || status == ApplicationStatus.REJECTED) { "이미 검토 중이거나 승인된 신청입니다." }

        this.status = ApplicationStatus.REVIEWING
    }

    fun approve() {
        check(status == ApplicationStatus.REVIEWING) { "검토 중인 신청이 아닙니다." }

        val reviewHistory = ReviewHistory.approve()

        this.status = ApplicationStatus.APPROVED
        this.reviewHistories.add(reviewHistory)
    }

    fun reject(reason: String) {
        check(status == ApplicationStatus.REVIEWING) { "검토 중인 신청이 아닙니다." }

        val reviewHistory = ReviewHistory.reject(reason)

        this.status = ApplicationStatus.REJECTED
        this.reviewHistories.add(reviewHistory)
    }
}