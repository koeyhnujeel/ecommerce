package com.zunza.ecommerce.application.stock.required

import com.zunza.ecommerce.domain.stock.Stock

interface StockRepository {
    fun save(stock: Stock): Stock
}