package com.zunza.ecommerce.adapter.persistence.stock

import com.zunza.ecommerce.application.stock.required.StockRepository
import com.zunza.ecommerce.domain.stock.Stock
import org.springframework.stereotype.Repository

@Repository
class StockRepositoryImpl(
    private val stockJpaRepository: StockJpaRepository
) : StockRepository {
    override fun save(stock: Stock): Stock {
        return stockJpaRepository.save(stock)
    }
}