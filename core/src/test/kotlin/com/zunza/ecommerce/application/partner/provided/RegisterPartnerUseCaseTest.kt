package com.zunza.ecommerce.application.partner.provided

import com.zunza.ecommerce.application.fixture.RegisterPartnerCommandFixture
import com.zunza.ecommerce.application.partner.required.PartnerRepository
import com.zunza.ecommerce.application.partner.service.PartnerCommandService
import com.zunza.ecommerce.domain.partner.Partner
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterPartnerUseCaseTest {
    private lateinit var partnerRepository: PartnerRepository
    private lateinit var registerPartnerUseCase: RegisterPartnerUseCase

    @BeforeEach
    fun setUp() {
        partnerRepository = mockk()
        registerPartnerUseCase = PartnerCommandService(partnerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerPartner() {
        val command = RegisterPartnerCommandFixture.createRegisterPartnerCommand()
        val partnerId = 1L

        val partner = mockk<Partner> {
            every { id } returns partnerId
        }

        mockkObject(Partner.Companion)

        every { Partner.register(any(), any(), any(), any()) } returns partner
        every { partnerRepository.save(any()) } returns partner

        val result = registerPartnerUseCase.registerPartner(command)

        result shouldBe partnerId
        verify(exactly = 1) {
            Partner.register(partnerId, command.partnerApplicationId, command.businessInfo, command.settlementAccount)
            partnerRepository.save(partner)
        }
    }
}
