package com.zunza.ecommerce.application.seller.required

import com.zunza.ecommerce.domain.seller.Seller

interface SellerRepository {
    fun save(seller: Seller): Seller
}