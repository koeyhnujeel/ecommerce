package com.zunza.ecommerce.config

import com.zunza.ecommerce.application.account.required.EmailSender
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@TestConfiguration
class TestConfiguration {
    @Bean
    fun emailSender(): EmailSender = object : EmailSender {
        override fun send(email: String, title: String, message: String) {
            println("[Test] EmailSender Sending email: $email")
        }
    }

    @Bean
    fun testS3Client(localStackContainer: LocalStackContainer): S3Client {
        val s3Client = S3Client.builder()
            .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localStackContainer.accessKey,
                        localStackContainer.secretKey
                    )
                )
            )
            .region(Region.of(localStackContainer.region))
            .forcePathStyle(true)
            .build()

        s3Client.createBucket { it.bucket("test-bucket") }

        return s3Client
    }
}