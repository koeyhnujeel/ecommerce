package com.zunza.ecommerce.application.stock.service

import com.zunza.ecommerce.application.stock.provided.StockRegister
import com.zunza.ecommerce.application.stock.required.StockRepository
import com.zunza.ecommerce.application.stock.service.command.RegisterStockCommand
import com.zunza.ecommerce.domain.stock.Stock
import org.springframework.stereotype.Service

@Service
class StockRegistrationService(
    private val stockRepository: StockRepository
) : StockRegister {
    override fun register(command: RegisterStockCommand) {
        val stock = Stock.register(command.toSpec())

        stockRepository.save(stock)
    }
}