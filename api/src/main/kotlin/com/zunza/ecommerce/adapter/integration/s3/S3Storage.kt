package com.zunza.ecommerce.adapter.integration.s3

import com.zunza.ecommerce.application.asset.required.ImageStorage
import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.*

val logger = KotlinLogging.logger { }

@Component
class S3Storage(
    private val s3Client: S3Client,

    @param:Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,

    @param:Value("\${cloud.aws.region.static}")
    private val region: String
) : ImageStorage {
    override fun upload(command: UploadImageCommand): String {
        try {
            val key = generateKey(command.originalFilename)

            val request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(command.contentType)
                .build()

            command.imageInputStream.use { inputStream ->
                s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, command.contentLength)
                )
            }

            return getImageUrl(key)

        } catch (e: S3Exception) {
            logger.warn { "이미지 업로드 실패 (AWS 오류): ${e.awsErrorDetails()}" }
            throw e
        } catch (e: SdkClientException) {
            logger.warn { "이미지 업로드 실패 (네트워크/재시도 초과): $e" }
            throw e
        } catch (e: Exception) {
            logger.warn { "이미지 업로드 실패 (알 수 없는 오류): $e" }
            throw e
        }
    }

    override fun uploadAll(commands: List<UploadImageCommand>): List<String> {
        return commands.map { upload(it) }
    }

    private fun generateKey(originalFilename: String): String {
        val uuid = UUID.randomUUID().toString().replace("-", "_")
        val extension = originalFilename.substringAfterLast('.', "")

        return "$uuid.$extension"
    }

    private fun getImageUrl(key: String) =
        "https://$bucket.s3.$region.amazonaws.com/${key}"
}