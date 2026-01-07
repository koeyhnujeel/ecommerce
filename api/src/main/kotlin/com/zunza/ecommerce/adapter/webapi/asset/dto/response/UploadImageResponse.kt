package com.zunza.ecommerce.adapter.webapi.asset.dto.response

data class UploadImageResponse(
    val imageUrl: String
) {
    companion object {
        fun from(imageUrl: String) = UploadImageResponse(imageUrl)
    }
}
