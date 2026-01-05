package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.fixture.SubmitCommandFixture
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.service.SellerApplicationRegistrationService
import com.zunza.ecommerce.domain.onboarding.DuplicateBusinessNumberException
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SellerApplicationRegisterTest {

    private lateinit var sellerApplicationRepository: SellerApplicationRepository
    private lateinit var sellerApplicationRegister: SellerApplicationRegister

    @BeforeEach
    fun setUp() {
        sellerApplicationRepository = mockk()
        sellerApplicationRegister = SellerApplicationRegistrationService(sellerApplicationRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun submit() {
        val command = SubmitCommandFixture.createSubmitCommand()
        val sellerApplication = mockk<SellerApplication> {
            every { id } returns 1L
        }

        mockkObject(SellerApplication.Companion)

        every { SellerApplication.submit(
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
        ) } returns sellerApplication
        every { sellerApplicationRepository.existsByBusinessNumber(any()) } returns false
        every { sellerApplicationRepository.save(any()) } returns sellerApplication

        val result = sellerApplicationRegister.submit(command)

        result.partnerApplicationId shouldBe 1L

        verify(exactly = 1) {
            sellerApplicationRepository.existsByBusinessNumber(command.businessNumber)
            sellerApplicationRepository.save(any())
        }
    }

    @Test
    fun submitFailDuplicateBusinessNumber() {
        val command = SubmitCommandFixture.createSubmitCommand()

        every { sellerApplicationRepository.existsByBusinessNumber(any()) } returns true

        shouldThrow<DuplicateBusinessNumberException> {
            sellerApplicationRegister.submit(command)
        }

        verify(exactly = 1) { sellerApplicationRepository.existsByBusinessNumber(command.businessNumber) }
        verify(exactly = 0) { sellerApplicationRepository.save(any()) }
    }
}
