package com.zunza.ecommerce.adapter.webapi.product.dto.response

data class RegisterProductResponse(
    val productId: Long
) {
    companion object {
        fun from(productId: Long) = RegisterProductResponse(productId)
    }
}