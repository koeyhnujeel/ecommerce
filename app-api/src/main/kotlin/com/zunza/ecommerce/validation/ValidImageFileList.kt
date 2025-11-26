package com.zunza.ecommerce.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ImageFileListValidator::class])
@MustBeDocumented
annotation class ValidImageFileList(
    val message: String = "유효하지 않은 이미지 파일 목록입니다",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val maxSizeMB: Long = 3,
    val allowedExtensions: Array<String> = ["jpg", "jpeg", "png", "webp"],
    val maxCount: Int = 15,
    val required: Boolean = false
)
