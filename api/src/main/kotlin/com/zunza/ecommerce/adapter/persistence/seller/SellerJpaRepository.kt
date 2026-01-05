package com.zunza.ecommerce.adapter.persistence.seller

import com.zunza.ecommerce.domain.seller.Seller
import org.springframework.data.jpa.repository.JpaRepository

interface SellerJpaRepository : JpaRepository<Seller, Long> {
}