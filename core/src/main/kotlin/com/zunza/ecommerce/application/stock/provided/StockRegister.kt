package com.zunza.ecommerce.application.stock.provided

import com.zunza.ecommerce.application.stock.service.command.RegisterStockCommand

interface StockRegister {
    fun register(command: RegisterStockCommand)
}