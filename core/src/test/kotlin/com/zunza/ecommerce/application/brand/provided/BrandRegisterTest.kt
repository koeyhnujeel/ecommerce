package com.zunza.ecommerce.application.brand.provided

import com.zunza.ecommerce.application.brand.required.BrandRepository
import com.zunza.ecommerce.application.brand.service.BrandRegistrationService
import com.zunza.ecommerce.domain.brand.Brand
import com.zunza.ecommerce.domain.brand.BrandInfo
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BrandRegisterTest {
    private lateinit var brandRepository: BrandRepository
    private lateinit var brandRegister: BrandRegister

    @BeforeEach
    fun setUp() {
        brandRepository = mockk()
        brandRegister = BrandRegistrationService(brandRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerBrand() {
        val sellerId = 1L
        val brandInfo = BrandInfo("나이키", "Nike","Just Do It")
        val brand = mockk<Brand>()

        mockkObject(Brand.Companion)

        every { Brand.register(any(), any()) } returns brand
        every { brandRepository.save(any()) } returns mockk()

        brandRegister.registerBrand(sellerId, brandInfo)

        verify(exactly = 1) {
            Brand.register(sellerId, brandInfo)
            brandRepository.save(brand)
        }
    }
}
