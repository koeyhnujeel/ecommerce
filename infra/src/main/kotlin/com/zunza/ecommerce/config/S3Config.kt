package com.zunza.ecommerce.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
@Profile("!test")
@EnableConfigurationProperties(S3Properties::class)
class S3Config(
    private val s3Properties: S3Properties
) {
    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(s3Properties.s3.region))
            .credentialsProvider(ProfileCredentialsProvider.create(s3Properties.s3.profile))
            .build()
    }
}
