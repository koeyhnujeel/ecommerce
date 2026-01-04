package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.fixture.RejectCommandFixture
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

class RejectUseCaseTest {
    private lateinit var partnerApplicationRepository: PartnerApplicationRepository
    private lateinit var rejectUseCase: RejectUseCase

    @BeforeEach
    fun setUp() {
        partnerApplicationRepository = mockk()
        rejectUseCase = PartnerApplicationCommandService(partnerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun reject() {
        val command = RejectCommandFixture.createRejectCommand()
        val partnerApplication = mockk<PartnerApplication>(relaxed = true)

        every { partnerApplicationRepository.findByIdOrThrow(command.partnerApplicationId) } returns partnerApplication
        every { partnerApplicationRepository.save(any()) } returns mockk()

        rejectUseCase.reject(command)

        verify(exactly = 1) {
            partnerApplicationRepository.findByIdOrThrow(command.partnerApplicationId)
            partnerApplication.reject(command.reason)
            partnerApplicationRepository.save(partnerApplication)
        }
    }

    @Test
    fun rejectFailPartnerApplicationNotFound() {
        val command = RejectCommandFixture.createRejectCommand()
        val partnerApplication = mockk<PartnerApplication>(relaxed = true)

        every { partnerApplicationRepository.findByIdOrThrow(any()) } throws PartnerApplicationNotFoundException()

        shouldThrow<PartnerApplicationNotFoundException> {
            rejectUseCase.reject(command)
        }.message shouldBe "존재하지 않는 신청서입니다."

        verify(exactly = 1) {
            partnerApplicationRepository.findByIdOrThrow(command.partnerApplicationId)
        }

        verify(exactly = 0) {
            partnerApplication.reject(command.reason)
            partnerApplicationRepository.save(partnerApplication)
        }
    }
}
