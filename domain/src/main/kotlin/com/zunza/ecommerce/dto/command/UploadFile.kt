package com.zunza.ecommerce.dto.command

data class UploadFile(
    val originalFilename: String,
    val contentType: String,
    val size: Long,
    val bytes: ByteArray
)
