package com.zunza.ecommerce.port

import com.zunza.ecommerce.dto.command.UploadFile

interface ImageStorage {
    fun upload(path: String, file: UploadFile): String

    fun uploadAll(path: String, files: List<UploadFile>): List<String>

    fun delete(fileUrl: String)

    fun deleteAll(fileUrls: List<String>)
}
