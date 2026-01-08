package com.zunza.ecommerce.adapter.webapi.asset

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.asset.dto.response.UploadImageResponse
import com.zunza.ecommerce.application.asset.provided.ImageManager
import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/assets")
class AssetApi(
    private val imageManager: ImageManager
) {
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/products/images/upload")
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadProductMainImage(@RequestParam image: MultipartFile): ApiResponse<UploadImageResponse> {
        val command = UploadImageCommand(
            image.originalFilename!!,
            image.contentType!!,
            image.size,
            image.inputStream
        )

        val imageUrl = imageManager.uploadProductMainImage(command)

        return ApiResponse.success(UploadImageResponse.from(imageUrl))
    }

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/products/images/upload-all")
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadProductDetailImages(@RequestParam images: List<MultipartFile>): ApiResponse<List<UploadImageResponse>> {
        val commands = images.map { image ->
            UploadImageCommand(
                image.originalFilename!!,
                image.contentType!!,
                image.size,
                image.inputStream
            )
        }

        val imageUrls = imageManager.uploadProductDetailImages(commands)
        val response = imageUrls.map { UploadImageResponse.from(it) }

        return ApiResponse.success(response)
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/products/images/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProductImage(@RequestParam imageUrl: String): ApiResponse<Any> {
        imageManager.deleteImage(imageUrl)

        return ApiResponse.success()
    }
}