package com.zunza.ecommerce.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class ImageFileListValidator : ConstraintValidator<ValidImageFileList, List<MultipartFile>> {
    private lateinit var allowedExtensions: List<String>
    private var maxSizeInBytes: Long = 0
    private var maxCount: Int = 0
    private var required = false

    override fun initialize(constraintAnnotation: ValidImageFileList) {
        this.allowedExtensions = constraintAnnotation.allowedExtensions.map { it }
        this.maxSizeInBytes = constraintAnnotation.maxSizeMB * 1024 * 1024
        this.maxCount = constraintAnnotation.maxCount
        this.required = constraintAnnotation.required
    }

    override fun isValid(
        files: List<MultipartFile>?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (files.isNullOrEmpty()) {
            if (required) {
                context.disableDefaultConstraintViolation()
                context.buildConstraintViolationWithTemplate(
                    "이미지 파일들을 첨부해 주세요."
                ).addConstraintViolation()

                return false
            }
            return true
        }

        if (files.size > maxCount) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "이미지 파일은 최대 ${maxCount}개까지 업로드 가능합니다."
            ).addConstraintViolation()
            return false
        }

        files.forEachIndexed { index, file ->
            if (!validateSingleFile(file, index, context)) {
                return false
            }
        }

        return true
    }

    private fun validateSingleFile(
        file: MultipartFile,
        index: Int,
        context: ConstraintValidatorContext
    ): Boolean {
        if (file.size > maxSizeInBytes) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "파일 #${index + 1}: 최대 이미지 파일 크기는 ${maxSizeInBytes / (1024 * 1024)}MB 입니다."
            ).addConstraintViolation()
            return false
        }

        val originalFilename = file.originalFilename ?: return false
        val extension = originalFilename
            .substringAfterLast('.')
            .lowercase()

        if (extension !in allowedExtensions) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "파일 #${index + 1}: 지원하지 않는 이미지 파일 형식입니다."
            ).addConstraintViolation()
            return false
        }

        return true
    }
}
