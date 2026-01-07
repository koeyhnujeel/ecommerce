package com.zunza.ecommerce.application.asset.service.dto.command

import java.io.InputStream

data class UploadImageCommand(
    val originalFilename: String,
    val contentType: String,
    val contentLength: Long,
    val imageInputStream: InputStream
)
