package com.zunza.ecommerce.adapter.integration.s3

import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception

@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class S3StorageTest(
    private val s3Client: S3Client,
    private val s3Storage: S3Storage
) {
    @Test
    fun `상품 이미지 업로드에 성공하면 S3에 저장된다`() {
        val command = UploadImageCommand(
            originalFilename = "main.png",
            contentType = MediaType.IMAGE_PNG_VALUE,
            contentLength = "main".length.toLong(),
            imageInputStream = "main".toByteArray().inputStream()
        )

        val imageUrl = s3Storage.upload(command)

        imageUrl shouldStartWith "https://test-bucket.s3.ap-northeast-2.amazonaws.com/"

        val key = extractKeyFromUrl(imageUrl)
        val request = HeadObjectRequest.builder()
            .bucket("test-bucket")
            .key(key)
            .build()

        s3Client.headObject(request).sdkHttpResponse().isSuccessful shouldBe true
    }

    @Test
    fun `상품 이미지 업로드 중 존재하지 않는 버킷이면 예외가 발생한다`() {
        val storage = S3Storage(s3Client, "missing-bucket", "ap-northeast-2")
        val command = UploadImageCommand(
            originalFilename = "main.png",
            contentType = MediaType.IMAGE_PNG_VALUE,
            contentLength = "main".length.toLong(),
            imageInputStream = "main".toByteArray().inputStream()
        )

        shouldThrow<S3Exception> {
            storage.upload(command)
        }
    }

    @Test
    fun `상품 이미지 삭제에 성공하면 S3에서 제거된다`() {
        val command = UploadImageCommand(
            originalFilename = "detail.png",
            contentType = MediaType.IMAGE_PNG_VALUE,
            contentLength = "detail".length.toLong(),
            imageInputStream = "detail".toByteArray().inputStream()
        )

        val imageUrl = s3Storage.upload(command)

        s3Storage.delete(imageUrl)

        val key = extractKeyFromUrl(imageUrl)
        val request = HeadObjectRequest.builder()
            .bucket("test-bucket")
            .key(key)
            .build()

        shouldThrow<S3Exception> {
            s3Client.headObject(request)
        }
    }

    @Test
    fun `상품 이미지 삭제 중 존재하지 않는 버킷이면 예외가 발생한다`() {
        val storage = S3Storage(s3Client, "missing-bucket", "ap-northeast-2")
        val imageUrl = "https://test-bucket.s3.ap-northeast-2.amazonaws.com/path/to/file.png"

        shouldThrow<S3Exception> {
            storage.delete(imageUrl)
        }
    }

    private fun extractKeyFromUrl(fileUrl: String) =
        fileUrl.substringAfter(".amazonaws.com/")
}
