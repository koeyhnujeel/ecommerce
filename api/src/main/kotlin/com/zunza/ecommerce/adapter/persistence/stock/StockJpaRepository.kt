package com.zunza.ecommerce.adapter.persistence.stock

import com.zunza.ecommerce.domain.stock.Stock
import org.springframework.data.jpa.repository.JpaRepository

interface StockJpaRepository : JpaRepository<Stock, Long> {
}