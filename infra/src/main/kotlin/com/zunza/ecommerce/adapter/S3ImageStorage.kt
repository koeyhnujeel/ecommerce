package com.zunza.ecommerce.adapter

import com.zunza.ecommerce.config.S3Properties
import com.zunza.ecommerce.dto.command.UploadFile
import com.zunza.ecommerce.port.ImageStorage
import com.zunza.ecommerce.support.exception.ErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest
import software.amazon.awssdk.services.s3.model.ObjectIdentifier
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.UUID

val logger = KotlinLogging.logger {  }

@Component
class S3ImageStorage(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) : ImageStorage {

    override fun upload(path: String, file: UploadFile): String {
        try {
            return putObject(path, file)
        } catch (e: S3Exception) {
            logger.warn { "파일 업로드 실패: ${e.message}" }
            throw ErrorCode.FILE_UPLOAD_FAILED.exception()
        }
    }

    override fun uploadAll(path: String, files: List<UploadFile>): List<String> {
        val uploadedUrls = mutableListOf<String>()

        try {
            files.forEach { file ->
                val url = putObject(path, file)
                uploadedUrls.add(url)
            }

            return uploadedUrls
        } catch (e: S3Exception) {
            rollbackUploads(uploadedUrls)
            logger.warn { "파일 업로드 실패: ${e.message}" }
            throw ErrorCode.FILE_UPLOAD_FAILED.exception()
        }
    }

    override fun delete(fileUrl: String) {
        try {
            deleteObject(fileUrl)
        } catch (e: S3Exception) {
            logger.warn { "파일 삭제 실패: ${e.message}" }
        }
    }

    override fun deleteAll(fileUrls: List<String>) {
        try {
            deleteObjects(fileUrls)
        } catch (e: S3Exception) {
            logger.warn { "파일 삭제 실패: ${e.message}" }
        }
    }

    private fun putObject(path: String, file: UploadFile): String {
        val key = generateKey(path, file.originalFilename)

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(key)
            .contentType(file.contentType)
            .contentLength(file.size)
            .build()

        val requestBody = RequestBody.fromBytes(file.bytes)

        s3Client.putObject(putObjectRequest, requestBody)

        return buildFileUrl(key)
    }

    private fun deleteObject(fileUrl: String) {
        val key = extractKeyFromUrl(fileUrl)

        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .key(key)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    private fun deleteObjects(fileUrls: List<String>) {
        val identifiers = fileUrls.map { url ->
            ObjectIdentifier.builder()
                .key(extractKeyFromUrl(url))
                .build()
        }

        val deleteObjectsRequest = DeleteObjectsRequest.builder()
            .bucket(s3Properties.s3.bucket)
            .delete { deleteBuilder ->
                deleteBuilder.objects(identifiers).build()
            }
            .build()

        s3Client.deleteObjects(deleteObjectsRequest)
    }

    private fun rollbackUploads(fileUrls: List<String>) {
        try {
            deleteObjects(fileUrls)
        } catch (e: S3Exception) {
            logger.warn { "업로드 롤백 실패: ${e.message}" }
        }
    }

    private fun generateKey(path: String, originalFilename: String): String {
        val uuid = UUID.randomUUID().toString().replace("-", "_")
        val extension = originalFilename.substringAfterLast('.', "")

        return "$path/$uuid.$extension"
    }

    private fun buildFileUrl(key: String) =
        "https://${s3Properties.s3.bucket}.s3.${s3Properties.s3.region}.amazonaws.com/${key}"

    private fun extractKeyFromUrl(fileUrl: String) =
        fileUrl.substringAfter(".amazonaws.com/")
}
