package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.fixture.SubmitCommandFixture
import com.zunza.ecommerce.application.onboarding.required.PartnerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.PartnerApplicationCommandService
import com.zunza.ecommerce.domain.onboarding.DuplicateBusinessNumberException
import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SubmitUseCaseTest {

    private lateinit var partnerApplicationRepository: PartnerApplicationRepository
    private lateinit var submitUseCase: SubmitUseCase

    @BeforeEach
    fun setUp() {
        partnerApplicationRepository = mockk()
        submitUseCase = PartnerApplicationCommandService(partnerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun submit() {
        val command = SubmitCommandFixture.createSubmitCommand()
        val partnerApplication = mockk<PartnerApplication> {
            every { id } returns 1L
        }

        mockkObject(PartnerApplication.Companion)

        every { PartnerApplication.submit(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        ) } returns partnerApplication
        every { partnerApplicationRepository.existsByBusinessNumber(any()) } returns false
        every { partnerApplicationRepository.save(any()) } returns partnerApplication

        val result = submitUseCase.submit(command)

        result.partnerApplicationId shouldBe 1L

        verify(exactly = 1) {
            partnerApplicationRepository.existsByBusinessNumber(command.businessNumber)
            partnerApplicationRepository.save(any())
        }
    }

    @Test
    fun submitFailDuplicateBusinessNumber() {
        val command = SubmitCommandFixture.createSubmitCommand()

        every { partnerApplicationRepository.existsByBusinessNumber(any()) } returns true

        shouldThrow<DuplicateBusinessNumberException> {
            submitUseCase.submit(command)
        }

        verify(exactly = 1) { partnerApplicationRepository.existsByBusinessNumber(command.businessNumber) }
        verify(exactly = 0) { partnerApplicationRepository.save(any()) }
    }
}
