package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.PartnerApplicationCommandService
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartReviewUseCaseTest {
    private lateinit var partnerApplicationRepository: PartnerApplicationRepository
    private lateinit var startReviewUseCase: StartReviewUseCase

    @BeforeEach
    fun setUp() {
        partnerApplicationRepository = mockk()
        startReviewUseCase = PartnerApplicationCommandService(partnerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun startReview() {
        val partnerApplicationId = 1L
        val partnerApplication = mockk<PartnerApplication>(relaxed = true)

        every { partnerApplicationRepository.findByIdOrThrow(partnerApplicationId) } returns partnerApplication
        every { partnerApplicationRepository.save(any()) } returns mockk()

        startReviewUseCase.startReview(partnerApplicationId)

        verify(exactly = 1) {
            partnerApplicationRepository.findByIdOrThrow(partnerApplicationId)
            partnerApplication.startReview()
            partnerApplicationRepository.save(partnerApplication)
        }
    }
}
