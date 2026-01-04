package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.PartnerApplicationCommandService
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import com.zunza.ecommerce.domain.onboarding.PartnerApplicationNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ApproveUseCaseTest {
    private lateinit var partnerApplicationRepository: PartnerApplicationRepository
    private lateinit var approveUseCase: ApproveUseCase

    @BeforeEach
    fun setUp() {
        partnerApplicationRepository = mockk()
        approveUseCase = PartnerApplicationCommandService(partnerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun approve() {
        val partnerApplicationId = 1L
        val partnerApplication = mockk<PartnerApplication>(relaxed = true)

        every { partnerApplicationRepository.findByIdOrThrow(partnerApplicationId) } returns partnerApplication
        every { partnerApplicationRepository.save(any()) } returns mockk()

        approveUseCase.approve(partnerApplicationId)

        verify(exactly = 1) {
            partnerApplicationRepository.findByIdOrThrow(partnerApplicationId)
            partnerApplication.approve()
            partnerApplicationRepository.save(partnerApplication)
        }
    }

    @Test
    fun approveFailPartnerApplicationNotFound() {
        val partnerApplicationId = 1L
        val partnerApplication = mockk<PartnerApplication>(relaxed = true)

        every { partnerApplicationRepository.findByIdOrThrow(any()) } throws PartnerApplicationNotFoundException()

        shouldThrow<PartnerApplicationNotFoundException> {
            approveUseCase.approve(partnerApplicationId)
        }.message shouldBe "존재하지 않는 신청서입니다."

        verify(exactly = 1) {
            partnerApplicationRepository.findByIdOrThrow(partnerApplicationId)
        }

        verify(exactly = 0) {
            partnerApplication.approve()
            partnerApplicationRepository.save(partnerApplication)
        }
    }
}
