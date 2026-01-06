package com.zunza.ecommerce.application.stock.provided

import com.zunza.ecommerce.application.stock.required.StockRepository
import com.zunza.ecommerce.application.stock.service.StockRegistrationService
import com.zunza.ecommerce.application.stock.service.command.RegisterStockCommand
import com.zunza.ecommerce.domain.stock.Stock
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StockRegisterTest {
    private lateinit var stockRepository: StockRepository
    private lateinit var stockRegister: StockRegister

    @BeforeEach
    fun setUp() {
        stockRepository = mockk()
        stockRegister = StockRegistrationService(stockRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
        unmockkAll()
    }

    @Test
    fun `재고 등록은 스톡 생성과 저장으로 이루어진다`() {
        val command = RegisterStockCommand(productOptionId = 1L, quantity = 20)

        mockkObject(Stock.Companion)
        val registeredStock = mockk<Stock>()
        every { Stock.register(command.toSpec()) } returns registeredStock
        every { stockRepository.save(registeredStock) } returns registeredStock

        stockRegister.register(command)

        verify(exactly = 1) {
            Stock.register(command.toSpec())
            stockRepository.save(registeredStock)
        }
    }
}
