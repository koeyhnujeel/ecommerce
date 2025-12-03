package com.zunza.ecommerce.service

import com.zunza.ecommerce.domain.Product
import com.zunza.ecommerce.domain.ProductImage
import com.zunza.ecommerce.domain.ProductOption
import com.zunza.ecommerce.domain.enums.ImageType
import com.zunza.ecommerce.domain.enums.ProductStatus
import com.zunza.ecommerce.dto.command.ProductRegisterCommand
import com.zunza.ecommerce.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun register(
        command: ProductRegisterCommand,
        mainImageUrl: String,
        detailImageUrls: List<String>
    ) {
        val allImages = createProductImage(mainImageUrl, detailImageUrls)

        val product = Product.of(
            command.brandId,
            command.categoryId,
            command.name,
            command.description,
            command.price,
            ProductStatus.ON_SALE,
            command.options.map { ProductOption.from(it) },
            allImages
        )

        productRepository.save(product)
    }

    private fun createProductImage(
        mainImageUrl: String,
        detailImageUrls: List<String>
    ): MutableList<ProductImage> {
        val allImages = mutableListOf<ProductImage>()
        allImages.add(ProductImage.of(mainImageUrl, ImageType.MAIN, 0))

        detailImageUrls.forEachIndexed { index, url ->
            allImages.add(ProductImage.of(url, ImageType.DETAIL, index + 1))
        }

        return allImages
    }
}
