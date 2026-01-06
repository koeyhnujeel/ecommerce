package com.zunza.ecommerce.domain.prodcut

enum class ImageType {
    MAIN, DETAIL;

    companion object {
        fun from(value: String) =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw InvalidImageRoleException()
    }
}