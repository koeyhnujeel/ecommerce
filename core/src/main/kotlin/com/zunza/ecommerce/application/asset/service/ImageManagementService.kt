package com.zunza.ecommerce.application.asset.service

import com.zunza.ecommerce.application.asset.provided.ImageManager
import com.zunza.ecommerce.application.asset.required.ImageStorage
import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import org.springframework.stereotype.Service

@Service
class ImageManagementService(
    private val imageStorage: ImageStorage,
) : ImageManager {
    override fun uploadProductMainImage(command: UploadImageCommand): String {
        return imageStorage.upload(command)
    }

    override fun uploadProductDetailImages(commands: List<UploadImageCommand>): List<String> {
        return imageStorage.uploadAll(commands)
    }
}