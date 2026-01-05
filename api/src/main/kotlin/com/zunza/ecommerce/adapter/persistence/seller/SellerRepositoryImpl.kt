package com.zunza.ecommerce.adapter.persistence.seller

import com.zunza.ecommerce.application.seller.required.SellerRepository
import com.zunza.ecommerce.domain.seller.Seller
import org.springframework.stereotype.Repository

@Repository
class SellerRepositoryImpl(
    private val sellerJpaRepository: SellerJpaRepository
) : SellerRepository {
    override fun save(seller: Seller): Seller {
        return sellerJpaRepository.save(seller)
    }
}