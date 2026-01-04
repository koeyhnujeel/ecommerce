package com.zunza.ecommerce.domain.onboarding

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class PartnerApplicationTest {
    @Test
    fun submit() {
        val partnerApplication = partnerApplicationSubmit()

        partnerApplication.applicantInfo.representativeName shouldBe "홍길동"
        partnerApplication.applicantInfo.contractEmail.address shouldBe "zunza@email.com"
        partnerApplication.applicantInfo.representativePhone shouldBe "01011112222"
        partnerApplication.businessInfo.businessNumber shouldBe "1234567890"
        partnerApplication.businessInfo.companyName shouldBe "홍컴퍼니"
        partnerApplication.brandInfo.nameKor shouldBe "홍"
        partnerApplication.brandInfo.nameEng shouldBe "Hong"
        partnerApplication.brandInfo.description shouldBe "다양한 의류를 선보이는"
        partnerApplication.settlementAccount.bankName shouldBe "한국은행"
        partnerApplication.settlementAccount.accountNumber shouldBe "11111111111111"
        partnerApplication.settlementAccount.accountHolder shouldBe "홍길동"
        partnerApplication.status shouldBe ApplicationStatus.SUBMITTED
        partnerApplication.submittedAt shouldNotBe null
        partnerApplication.reviewHistories shouldHaveSize 0
    }

    @Test
    fun submitFailInvalidApplicantInfo() {
        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(representativeName = "")
        }.message shouldBe "대표자 이름은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(contractEmail = "")
        }.message shouldBe "이메일은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(contractEmail = "zunza")
        }.message shouldBe "잘못된 이메일 형식입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(representativePhone = "")
        }.message shouldBe "대표자 번호는 필수입니다."
    }

    @Test
    fun submitFailInvalidBusinessInfo() {
        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(businessNumber = "")
        }.message shouldBe "사업자등록번호 형식이 올바르지 않습니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(companyName = "")
        }.message shouldBe "업체명은 필수입니다."
    }

    @Test
    fun submitFailInvalidBrandInfo() {
        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(brandNameKor = "")
        }.message shouldBe "브랜드 이름(국문)은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(brandNameEng = "")
        }.message shouldBe "브랜드 이름(영문)은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(brandDescription = "")
        }.message shouldBe "브랜드 소개는 필수입니다."
    }

    @Test
    fun submitFailInvalidBankAccount() {
        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(bankName = "")
        }.message shouldBe "은행은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(accountNumber = "")
        }.message shouldBe "계좌번호는 필수입니다."

        shouldThrow<IllegalArgumentException> {
            partnerApplicationSubmit(accountHolder = "")
        }.message shouldBe "예금주명은 필수입니다."
    }

    @Test
    fun startReview() {
        val partnerApplication = partnerApplicationSubmit()

        partnerApplication.startReview()

        partnerApplication.status shouldBe ApplicationStatus.REVIEWING
    }

    @Test
    fun startReviewFail() {
        val partnerApplication = partnerApplicationSubmit()
        partnerApplication.status = ApplicationStatus.REVIEWING

        shouldThrow<IllegalStateException> {
            partnerApplication.startReview()
        }.message shouldBe "이미 검토 중이거나 승인된 신청입니다."

        partnerApplication.status = ApplicationStatus.APPROVED

        shouldThrow<IllegalStateException> {
            partnerApplication.startReview()
        }.message shouldBe "이미 검토 중이거나 승인된 신청입니다."
    }

    @Test
    fun approve() {
        val partnerApplication = partnerApplicationSubmit()

        partnerApplication.startReview()

        partnerApplication.approve()

        partnerApplication.status shouldBe ApplicationStatus.APPROVED
        partnerApplication.reviewHistories[0].result shouldBe ReviewResult.APPROVED
        partnerApplication.reviewHistories[0].comment shouldBe "입점이 승인됐습니다."
        partnerApplication.reviewHistories[0].reviewedAt shouldNotBe null
    }

    @Test
    fun approveFailNotReviewing() {
        val partnerApplication = partnerApplicationSubmit()

        shouldThrow<IllegalStateException> {
            partnerApplication.approve()
        }.message shouldBe "검토 중인 신청이 아닙니다."
    }

    @Test
    fun reject() {
        val partnerApplication = partnerApplicationSubmit()

        partnerApplication.startReview()

        partnerApplication.reject("은행 정보 오기입")

        partnerApplication.status shouldBe ApplicationStatus.REJECTED
        partnerApplication.reviewHistories[0].result shouldBe ReviewResult.REJECTED
        partnerApplication.reviewHistories[0].comment shouldBe "은행 정보 오기입"
        partnerApplication.reviewHistories[0].reviewedAt shouldNotBe null
    }

    @Test
    fun rejectFailNotReviewing() {
        val partnerApplication = partnerApplicationSubmit()

        shouldThrow<IllegalStateException> {
            partnerApplication.reject("은행 정보 오기입")
        }.message shouldBe "검토 중인 신청이 아닙니다."
    }

    private fun partnerApplicationSubmit(
        accountId: Long = 1L,
        representativeName: String = "홍길동",
        contractEmail: String = "zunza@email.com",
        representativePhone: String = "01011112222",
        businessNumber: String = "1234567890",
        companyName: String = "홍컴퍼니",
        brandNameKor: String = "홍",
        brandNameEng: String = "Hong",
        brandDescription: String = "다양한 의류를 선보이는",
        bankName: String = "한국은행",
        accountNumber: String = "11111111111111",
        accountHolder: String = "홍길동"
    ): PartnerApplication {
        return PartnerApplication.submit(
            accountId = accountId,
            representativeName = representativeName,
            contractEmail = contractEmail,
            representativePhone = representativePhone,
            businessNumber = businessNumber,
            companyName = companyName,
            brandNameKor = brandNameKor,
            brandNameEng = brandNameEng,
            brandDescription = brandDescription,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolder = accountHolder,
        )
    }
}