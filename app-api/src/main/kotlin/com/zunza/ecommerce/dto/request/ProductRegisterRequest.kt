package com.zunza.ecommerce.dto.request

import com.zunza.ecommerce.dto.command.ProductRegisterCommand
import com.zunza.ecommerce.dto.command.ProductOptionCommand
import com.zunza.ecommerce.dto.command.UploadFile
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

data class ProductRegisterRequest(
    @field:Min(value = 1, message = "브랜드를 선택해 주세요.")
    val brandId: Long,
    @field:Min(value = 1, message = "카테고리를 선택해 주세요.")
    val categoryId: Long,
    @field:NotBlank(message = "상품명을 입력해 주세요.")
    @field:Size(min = 2, max = 30, message = "상품명은 2-30자 사이여야 합니다.")
    val name: String,
    @field:NotBlank(message = "상품 설명을 입력해 주세요.")
    @field:Size(min = 2, max = 1000, message = "상품 설명은 2-1000자 사이여야 합니다.")
    val description: String,
    @field:Min(value = 100, message = "상품 가격은 100원 이상이어야 합니다")
    @field:Max(value = 10_000_000, message = "상품 가격은 1000만원 이하여야 합니다")
    val price: BigDecimal,
    @field:Valid
    @field:NotEmpty(message = "상품 옵션은 최소 1개 이상이어야 합니다.")
    val options: List<ProductOptionRequest>
) {
    fun toCommand(
        mainImage: MultipartFile,
        detailImages: List<MultipartFile>
    ): ProductRegisterCommand {
        val optionCommands = this.options.map {
            it.toCommand()
        }

        val mainImage = UploadFile(
            originalFilename = mainImage.originalFilename!!,
            contentType = mainImage.contentType!!,
            size = mainImage.size,
            bytes = mainImage.bytes
        )

        val subImages = detailImages.map {
            UploadFile(
                it.originalFilename!!,
                it.contentType!!,
                it.size,
                it.bytes
            )
        }

        return ProductRegisterCommand(
            brandId = this.brandId,
            categoryId = this.categoryId,
            name = this.name,
            description = this.description,
            price = this.price,
            options = optionCommands,
            mainImage = mainImage,
            detailImages = subImages
        )
    }
}

data class ProductOptionRequest(
    @field:NotBlank(message = "사이즈를 입력해 주세요.")
    val size: String,
    @field:NotBlank(message = "색상 입력해 주세요.")
    val color: String,
    @field:Min(value = 0, message = "추가 가격은 0원 이상이어야 합니다.")
    val additionalPrice: BigDecimal,
    @field:NotNull(message = "재고 수량을 입력해 주세요.")
    @field:Min(value = 1, message = "재고 수량은 1개 이상이어야 합니다.")
    val stockQuantity: Int,
    @field:NotNull(message = "표시 순서를 입력해 주세요.")
    @field:Min(value = 1, message = "표시 순서는 1 이상이어야 합니다.")
    val displayOrder: Int
) {
    fun toCommand() = ProductOptionCommand(
        size = this.size,
        color = this.color,
        additionalPrice = this.additionalPrice,
        stockQuantity = this.stockQuantity,
        displayOrder = this.displayOrder
    )
}
