package com.zunza.ecommerce.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class ImageFilesValidator : ConstraintValidator<ValidImageFiles, List<MultipartFile>> {
    private lateinit var allowedExtensions: List<String>
    private lateinit var allowedMimeTypes: List<String>
    private var maxSizeInBytes: Long = 0
    private var minCount: Int = 0
    private var maxCount: Int = 0

    override fun initialize(constraintAnnotation: ValidImageFiles) {
        this.allowedExtensions = constraintAnnotation.allowedExtensions.map { it }
        this.allowedMimeTypes = constraintAnnotation.allowedMimeTypes.map { it }
        this.maxSizeInBytes = constraintAnnotation.maxSizeMB * 1024 * 1024
        this.minCount = constraintAnnotation.minCount
        this.maxCount = constraintAnnotation.maxCount
    }

    override fun isValid(
        files: List<MultipartFile>?,
        context: ConstraintValidatorContext
    ): Boolean {
        if (files.isNullOrEmpty()) {
            return true
        }

        if (files.size < minCount) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "이미지 파일은 최소 ${minCount}개 이상 업로드 해주세요."
            ).addConstraintViolation()
            return false
        }

        if (files.size > maxCount) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "이미지 파일은 최대 ${maxCount}개까지 업로드 가능합니다."
            ).addConstraintViolation()
            return false
        }

        files.forEachIndexed { index, file ->
            if (!validateFile(file, index, context)) {
                return false
            }
        }

        return true
    }

    private fun validateFile(
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
            .substringAfterLast('.', "")
            .lowercase()

        if (extension !in allowedExtensions) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "파일 #${index + 1}: 지원하지 않는 이미지 파일 형식입니다."
            ).addConstraintViolation()
            return false
        }

        val contentType = file.contentType ?: return false

        if (contentType !in allowedMimeTypes) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate(
                "파일 #${index + 1}: 유효하지 않은 이미 파일입니다."
            ).addConstraintViolation()

            return false
        }

        return true
    }
}
