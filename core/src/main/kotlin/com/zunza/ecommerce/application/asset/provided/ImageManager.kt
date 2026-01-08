package com.zunza.ecommerce.application.asset.provided

import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand

interface ImageManager {
    fun uploadProductMainImage(command: UploadImageCommand): String

    fun uploadProductDetailImages(commands: List<UploadImageCommand>): List<String>

    fun deleteImage(imageUrl: String)
}