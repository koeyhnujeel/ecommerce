package com.zunza.ecommerce.adapter.webapi.product

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.product.dto.request.RegisterProductRequest
import com.zunza.ecommerce.adapter.webapi.product.dto.response.RegisterProductResponse
import com.zunza.ecommerce.application.product.provided.ProductRegister
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductApi(
    private val productRegister: ProductRegister
) {
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterProductRequest): ApiResponse<RegisterProductResponse> {
        val productId = productRegister.register(request.toCommand())

        return ApiResponse.success(RegisterProductResponse.from(productId))
    }
}