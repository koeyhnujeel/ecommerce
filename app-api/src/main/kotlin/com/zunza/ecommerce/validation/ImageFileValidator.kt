package com.zunza.ecommerce.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class ImageFileValidator : ConstraintValidator<ValidImageFile, MultipartFile> {
    private lateinit var allowedExtensions: List<String>
    private var maxSizeInBytes: Long = 0
    private var required: Boolean = true

    override fun initialize(constraintAnnotation: ValidImageFile) {
        this.allowedExtensions = constraintAnnotation.allowedExtensions.map { it }
        this.maxSizeInBytes = constraintAnnotation.maxSizeMB * 1024 * 1024
        this.required = constraintAnnotation.required
    }

    override fun isValid(
        file: MultipartFile?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (file == null || file.isEmpty) {
            if (required) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "이미지 파일을 첨부해 주세요."
                ).addConstraintViolation()

                return false
            }
            return true
        }

        if (!validateFileSize(file, context)) {
            return false
        }

        if (!validateFileExtension(file, context)) {
            return false
        }

        return true
    }

    private fun validateFileSize(
        file: MultipartFile,
        context: ConstraintValidatorContext
    ): Boolean {
        if (file.size > maxSizeInBytes) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "최대 이미지 파일 크기는 ${maxSizeInBytes / (1024 * 1024)}MB 입니다. "
            ).addConstraintViolation()

            return false
        }
        return true
    }

    private fun validateFileExtension(
        file: MultipartFile,
        context: ConstraintValidatorContext
    ): Boolean {
        val originalFilename = file.originalFilename ?: return false
        val extension = originalFilename
            .substringAfterLast('.')
            .lowercase()

        if (extension.isEmpty() || extension !in allowedExtensions) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "지원하지 않는 이미지 파일 형식입니다."
            ).addConstraintViolation()

            return false
        }
        return true
    }
}
