package com.zunza.ecommerce.application.seller.provided

import com.zunza.ecommerce.application.seller.required.SellerRepository
import com.zunza.ecommerce.application.seller.service.SellerRegistrationService
import com.zunza.ecommerce.domain.seller.BusinessInfo
import com.zunza.ecommerce.domain.seller.Seller
import com.zunza.ecommerce.domain.shared.BankAccount
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SellerRegisterTest {
    private lateinit var sellerRepository: SellerRepository
    private lateinit var sellerRegister: SellerRegister

    @BeforeEach
    fun setUp() {
        sellerRepository = mockk()
        sellerRegister = SellerRegistrationService(sellerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerPartner() {
        val sellerId = 1L
        val sellerApplicationId = 22L
        val businessInfo = BusinessInfo("1112223333", "컴퍼니")
        val settlementAccount = BankAccount("하나은행", "11122233344555", "홍길동")

        val seller = mockk<Seller> {
            every { id } returns sellerId
        }

        mockkObject(Seller.Companion)

        every { Seller.register(any(), any(), any(), any()) } returns seller
        every { sellerRepository.save(any()) } returns seller

        val result = sellerRegister.register(sellerId, sellerApplicationId, businessInfo, settlementAccount)

        result shouldBe sellerId
        verify(exactly = 1) {
            Seller.register(sellerId, sellerApplicationId, businessInfo, settlementAccount)
            sellerRepository.save(seller)
        }
    }
}
