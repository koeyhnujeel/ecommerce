package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.brand.provided.BrandRegister
import com.zunza.ecommerce.application.fixture.RejectCommandFixture
import com.zunza.ecommerce.application.onboarding.required.SellerApplicationRepository
import com.zunza.ecommerce.application.onboarding.required.findByIdOrThrow
import com.zunza.ecommerce.application.onboarding.service.SellerApplicationProcessingService
import com.zunza.ecommerce.application.seller.provided.SellerRegister
import com.zunza.ecommerce.domain.onboarding.SellerApplication
import com.zunza.ecommerce.domain.onboarding.SellerApplicationNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SellerApplicationProcessorTest {
    private lateinit var sellerApplicationRepository: SellerApplicationRepository
    private lateinit var accountManager: AccountManager
    private lateinit var sellerRegister: SellerRegister
    private lateinit var brandRegister: BrandRegister
    private lateinit var sellerApplicationProcessor: SellerApplicationProcessor

    @BeforeEach
    fun setUp() {
        sellerApplicationRepository = mockk()
        accountManager = mockk()
        sellerRegister = mockk()
        brandRegister = mockk()

        sellerApplicationProcessor = SellerApplicationProcessingService(
            sellerApplicationRepository,
            accountManager,
            sellerRegister,
            brandRegister,
        )
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun approve() {
        val sellerId = 1L
        val sellerApplicationId = 1L
        val sellerApplication = mockk<SellerApplication>(relaxed = true)

        every { sellerApplicationRepository.findByIdOrThrow(sellerApplicationId) } returns sellerApplication
        every { accountManager.grantPartnerRole(any()) } returns Unit
        every { sellerRegister.register(any(), any(), any(), any()) } returns sellerId
        every { brandRegister.registerBrand(any(), any()) } returns Unit

        sellerApplicationProcessor.approve(sellerApplicationId)

        verify(exactly = 1) {
            sellerApplicationRepository.findByIdOrThrow(sellerApplicationId)
            sellerApplication.approve()
            accountManager.grantPartnerRole(any())
            sellerRegister.register(any(), any(), any(), any())
            brandRegister.registerBrand(any(), any())
        }
    }

    @Test
    fun approveFailSellerApplicationNotFound() {
        val sellerApplicationId = 1L
        val sellerApplication = mockk<SellerApplication>(relaxed = true)

        every { sellerApplicationRepository.findByIdOrThrow(any()) } throws SellerApplicationNotFoundException()

        shouldThrow<SellerApplicationNotFoundException> {
            sellerApplicationProcessor.approve(sellerApplicationId)
        }.message shouldBe "존재하지 않는 신청서입니다."

        verify(exactly = 1) {
            sellerApplicationRepository.findByIdOrThrow(sellerApplicationId)
        }

        verify(exactly = 0) {
            sellerApplication.approve()
        }
    }

    @Test
    fun startReview() {
        val sellerApplicationId = 1L
        val sellerApplication = mockk<SellerApplication>(relaxed = true)

        every { sellerApplicationRepository.findByIdOrThrow(sellerApplicationId) } returns sellerApplication

        sellerApplicationProcessor.startReview(sellerApplicationId)

        verify(exactly = 1) {
            sellerApplicationRepository.findByIdOrThrow(sellerApplicationId)
            sellerApplication.startReview()
        }
    }

    @Test
    fun reject() {
        val command = RejectCommandFixture.createRejectCommand()
        val sellerApplication = mockk<SellerApplication>(relaxed = true)

        every { sellerApplicationRepository.findByIdOrThrow(command.sellerApplicationId) } returns sellerApplication

        sellerApplicationProcessor.reject(command)

        verify(exactly = 1) {
            sellerApplicationRepository.findByIdOrThrow(command.sellerApplicationId)
            sellerApplication.reject(command.reason)
        }
    }

    @Test
    fun rejectFailPartnerApplicationNotFound() {
        val command = RejectCommandFixture.createRejectCommand()
        val sellerApplication = mockk<SellerApplication>(relaxed = true)

        every { sellerApplicationRepository.findByIdOrThrow(any()) } throws SellerApplicationNotFoundException()

        shouldThrow<SellerApplicationNotFoundException> {
            sellerApplicationProcessor.reject(command)
        }.message shouldBe "존재하지 않는 신청서입니다."

        verify(exactly = 1) {
            sellerApplicationRepository.findByIdOrThrow(command.sellerApplicationId)
        }

        verify(exactly = 0) {
            sellerApplication.reject(command.reason)
        }
    }
}