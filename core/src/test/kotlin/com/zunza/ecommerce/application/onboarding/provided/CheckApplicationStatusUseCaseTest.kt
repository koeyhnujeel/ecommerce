package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.PartnerApplicationQueryService
import com.zunza.ecommerce.domain.onboarding.ApplicantInfo
import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import com.zunza.ecommerce.domain.onboarding.PartnerApplicationNotFoundException
import com.zunza.ecommerce.domain.shared.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class CheckApplicationStatusUseCaseTest {
    private lateinit var partnerApplicationRepository: PartnerApplicationRepository
    private lateinit var checkApplicationStatusUseCase: CheckApplicationStatusUseCase

    @BeforeEach
    fun setUp() {
        partnerApplicationRepository = mockk()
        checkApplicationStatusUseCase = PartnerApplicationQueryService(partnerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun checkApplicationStatus() {
        val accountId = 1L
        val now = LocalDateTime.now()
        val partnerApplication = mockk<PartnerApplication> {
            every { id } returns 1L
            every { applicantInfo } returns ApplicantInfo("홍길동", Email("hong@email.com"), "01012345678")
            every { status } returns ApplicationStatus.SUBMITTED
            every { submittedAt } returns now
        }

        every { partnerApplicationRepository.findByAccountId(accountId) } returns partnerApplication

        val result = checkApplicationStatusUseCase.checkApplicationStatus(accountId)

        result.applicationId shouldBe 1L
        result.representativeName shouldBe "홍길동"
        result.status shouldBe ApplicationStatus.SUBMITTED
        result.submittedAt shouldBe now

        verify(exactly = 1) { partnerApplicationRepository.findByAccountId(accountId) }
    }

    @Test
    fun checkApplicationStatusFailNotExists() {
        val accountId = 1L
        every { partnerApplicationRepository.findByAccountId(accountId) } returns null

        shouldThrow<PartnerApplicationNotFoundException> {
            checkApplicationStatusUseCase.checkApplicationStatus(accountId)
        }

        verify(exactly = 1) { partnerApplicationRepository.findByAccountId(accountId) }
    }
}
