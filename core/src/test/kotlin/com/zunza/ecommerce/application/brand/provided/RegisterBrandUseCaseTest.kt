package com.zunza.ecommerce.application.brand.provided

import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.application.brand.service.BrandCommandService
import com.zunza.ecommerce.application.fixture.RegisterBrandCommandFixture
import com.zunza.ecommerce.domain.brand.Brand
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterBrandUseCaseTest {
    private lateinit var brandRepository: BrandRepository
    private lateinit var registerBrandUseCase: RegisterBrandUseCase

    @BeforeEach
    fun setUp() {
        brandRepository = mockk()
        registerBrandUseCase = BrandCommandService(brandRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerBrand() {
        val command = RegisterBrandCommandFixture.createRegisterBrandCommand()
        val brand = mockk<Brand>()

        mockkObject(Brand.Companion)

        every { Brand.register(any(), any()) } returns brand
        every { brandRepository.save(any()) } returns mockk()

        registerBrandUseCase.registerBrand(command)

        verify(exactly = 1) {
            Brand.register(command.partnerId, command.brandInfo)
            brandRepository.save(brand)
        }
    }
}
