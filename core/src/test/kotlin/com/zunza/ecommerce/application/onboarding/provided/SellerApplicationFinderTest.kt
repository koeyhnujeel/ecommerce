package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.SellerApplicationQueryService
import com.zunza.ecommerce.domain.onboarding.ApplicantInfo
import com.zunza.ecommerce.domain.onboarding.ApplicationStatus
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import com.zunza.ecommerce.domain.onboarding.SellerApplicationNotFoundException
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

class SellerApplicationFinderTest {
    private lateinit var sellerApplicationRepository: SellerApplicationRepository
    private lateinit var sellerApplicationFinder: SellerApplicationFinder

    @BeforeEach
    fun setUp() {
        sellerApplicationRepository = mockk()
        sellerApplicationFinder = SellerApplicationQueryService(sellerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun getSellerApplicationStatus() {
        val accountId = 1L
        val now = LocalDateTime.now()
        val sellerApplication = mockk<SellerApplication> {
            every { id } returns 1L
            every { applicantInfo } returns ApplicantInfo("홍길동", Email("hong@email.com"), "01012345678")
            every { status } returns ApplicationStatus.SUBMITTED
            every { submittedAt } returns now
        }

        every { sellerApplicationRepository.findByAccountId(accountId) } returns sellerApplication

        val result = sellerApplicationFinder.getSellerApplicationStatus(accountId)

        result.sellerApplicationId shouldBe 1L
        result.representativeName shouldBe "홍길동"
        result.status shouldBe ApplicationStatus.SUBMITTED
        result.submittedAt shouldBe now

        verify(exactly = 1) { sellerApplicationRepository.findByAccountId(accountId) }
    }

    @Test
    fun getSellerApplicationStatusFailNotExists() {
        val accountId = 1L
        every { sellerApplicationRepository.findByAccountId(accountId) } returns null

        shouldThrow<SellerApplicationNotFoundException> {
            sellerApplicationFinder.getSellerApplicationStatus(accountId)
        }

        verify(exactly = 1) { sellerApplicationRepository.findByAccountId(accountId) }
    }
}
