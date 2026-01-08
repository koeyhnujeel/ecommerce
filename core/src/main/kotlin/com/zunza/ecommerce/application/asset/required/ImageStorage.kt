package com.zunza.ecommerce.application.asset.required

import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand

interface ImageStorage {
    fun upload(command: UploadImageCommand): String

    fun uploadAll(commands: List<UploadImageCommand>): List<String>

    fun delete(imageUrl: String)
}