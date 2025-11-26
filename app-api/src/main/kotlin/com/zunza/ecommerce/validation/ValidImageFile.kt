package com.zunza.ecommerce.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ImageFileValidator::class])
@MustBeDocumented
annotation class ValidImageFile(
    val message: String = "유효하지 않은 이미지 파일입니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val maxSizeMB: Long = 3,
    val allowedExtensions: Array<String> = ["jpg", "jpeg", "png", "webp"],
    val required: Boolean = true
)
