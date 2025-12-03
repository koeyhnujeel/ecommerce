package com.zunza.ecommerce.service

import com.zunza.ecommerce.dto.command.ProductRegisterCommand
import com.zunza.ecommerce.port.ImageStorage
import org.springframework.stereotype.Component

@Component
class ProductFacade(
    private val productService: ProductService,
    private val imageStorage: ImageStorage
) {
    fun registerProduct(command: ProductRegisterCommand) {
        val mainImageUrl = imageStorage.upload("products/main", command.mainImage)
        val detailImageUrls = imageStorage.uploadAll("products/detail", command.detailImages)

        try {
            productService.register(command, mainImageUrl, detailImageUrls)
        } catch (e: Exception) {
            imageStorage.deleteAll(listOf(mainImageUrl) + detailImageUrls)
            throw e
        }
    }
}
