package com.zunza.ecommerce.domain.onboarding

import com.zunza.ecommerce.domain.shared.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ApplicantInfoTest {
    @Test
    fun applicantInfo() {
        val applicantInfo = ApplicantInfo(
            "홍길동",
            Email("hong@email.com"),
            "01011112222"
        )

        applicantInfo.representativeName shouldBe "홍길동"
        applicantInfo.contactEmail.address shouldBe "hong@email.com"
        applicantInfo.representativePhone shouldBe "01011112222"
    }

    @Test
    fun applicantInfoFail() {
        shouldThrow<IllegalArgumentException> {
            ApplicantInfo("", Email("hong@email.com"), "01011112222")
        }.message shouldBe "대표자 이름은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            ApplicantInfo("홍길동", Email("hong@email.com"), "")
        }.message shouldBe "대표자 번호는 필수입니다."
    }
}