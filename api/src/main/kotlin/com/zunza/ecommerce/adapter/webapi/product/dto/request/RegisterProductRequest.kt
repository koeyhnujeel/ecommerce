package com.zunza.ecommerce.adapter.webapi.product.dto.request

import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductCommand
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductImageCommand
import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductOptionCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class RegisterProductRequest(
    @field:NotNull(message = "브랜드 선택은 필수입니다.")
    @field:Positive(message = "유효하지 않은 브랜드 입니다.")
    val brandId: Long,

    @field:NotEmpty(message = "카테고리는 최소 1개 이상 선택해야 합니다.")
    val categoryIds: List<Long>,

    @field:NotBlank(message = "상품명은 필수입니다.")
    val name: String,

    @field:NotBlank(message = "상품 설명은 필수입니다.")
    val description: String,

    @field:NotNull(message = "기본 가격은 필수입니다.")
    @field:Min(value = 100, message = "기본 가격은 100원 이상이어야 합니다.")
    @field:Digits(integer = 12, fraction = 0)
    val basePrice: BigDecimal,

    @field:Valid
    @field:NotEmpty(message = "상품 이미지는 최소 1장 이상 등록해야 합니다.")
    val images: List<RegisterProductImageRequest>,

    @field:Valid
    @field:NotEmpty(message = "상품 옵션은 최소 1개 이상 등록해야 합니다.")
    val options: List<RegisterProductOptionRequest>
) {
    fun toCommand() =
        RegisterProductCommand(
            brandId = brandId,
            categoryIds = categoryIds,
            name = name,
            description = description,
            basePrice = basePrice,
            images = images.map { it.toCommand() },
            options = options.map { it.toCommand() }
        )
}

data class RegisterProductImageRequest(
    @field:NotBlank(message = "이미지 URL은 필수입니다.")
    val url: String,

    @field:NotBlank(message = "이미지 타입은 필수입니다.")
    val type: String,

    @field:NotNull(message = "표시 순서는 필수입니다.")
    @field:PositiveOrZero(message = "표시 순서는 1 이상이어야 합니다.")
    val displayOrder: Int
) {
    fun toCommand() =
        RegisterProductImageCommand(
            url = url,
            type = type,
            displayOrder = displayOrder,
        )
}

data class RegisterProductOptionRequest(
    @field:NotBlank(message = "색상은 필수입니다.")
    val color: String,

    @field:NotBlank(message = "사이즈는 필수입니다.")
    val size: String,

    @field:Min(value = 0, message = "추가 가격은 0원 이상이어야 합니다.")
    val additionalPrice: BigDecimal,

    @field:NotNull(message = "재고 수량은 필수입니다.")
    @field:Min(value = 1, message = "재고 수량은 1개 이상이어야 합니다.")
    val quantity: Int
) {
    fun toCommand() =
        RegisterProductOptionCommand(
            color = color,
            size = size,
            additionalPrice = additionalPrice,
            quantity = quantity
        )
}
