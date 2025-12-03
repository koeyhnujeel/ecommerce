package com.zunza.ecommerce.controller

import com.zunza.ecommerce.dto.request.ProductRegisterRequest
import com.zunza.ecommerce.service.ProductFacade
import com.zunza.ecommerce.support.resopnse.ApiResponse
import com.zunza.ecommerce.validation.ValidImageFile
import com.zunza.ecommerce.validation.ValidImageFiles
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Validated
@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productFacade: ProductFacade
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerProduct(
        @Valid @RequestPart("request") request: ProductRegisterRequest,
        @ValidImageFile @RequestPart("mainImage") mainImage: MultipartFile,
        @ValidImageFiles @RequestPart("detailImages") detailImages: List<MultipartFile>
    ): ApiResponse<Any> {
        productFacade.registerProduct(request.toCommand(mainImage, detailImages))
        return ApiResponse.success()
    }
}
