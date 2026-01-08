package com.zunza.ecommerce.adapter.webapi.asset

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.adapter.integration.s3.S3Storage
import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.multipart

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class AssetApiTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val s3Storage: S3Storage
) {
    @Test
    @WithMockUser(authorities = ["ROLE_SELLER"])
    fun `상품 대표 이미지 업로드 시 이미지 URL을 반환한다`() {
        val mockFile = MockMultipartFile(
            "image",
            "main.png",
            MediaType.IMAGE_PNG_VALUE,
            "main-image".toByteArray()
        )

        val result = mockMvc.multipart("/api/assets/products/images/upload") {
            file(mockFile)
            contentType = MediaType.MULTIPART_FORM_DATA
        }.andExpect {
            status { isCreated() }
        }.andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)

        responseJson["success"].asBoolean() shouldBe true
        val imageUrl = responseJson["data"]["imageUrl"].asText()
        imageUrl shouldStartWith "https://test-bucket.s3.ap-northeast-2.amazonaws.com/"
    }

    @Test
    @WithMockUser(authorities = ["ROLE_SELLER"])
    fun `상품 상세 이미지 업로드 시 이미지 URL 목록을 반환한다`() {
        val first = MockMultipartFile(
            "images",
            "detail-1.png",
            MediaType.IMAGE_PNG_VALUE,
            "detail-1".toByteArray()
        )

        val second = MockMultipartFile(
            "images",
            "detail-2.png",
            MediaType.IMAGE_PNG_VALUE,
            "detail-2".toByteArray()
        )

        val result = mockMvc.multipart("/api/assets/products/images/upload-all") {
            file(first)
            file(second)
            contentType = MediaType.MULTIPART_FORM_DATA
        }.andExpect {
            status { isCreated() }
        }.andReturn()

        val responseJson = objectMapper.readTree(result.response.contentAsString)
        val dataNode = responseJson["data"]

        responseJson["success"].asBoolean() shouldBe true
        dataNode shouldHaveSize 2
        dataNode[0]["imageUrl"].asText() shouldStartWith "https://test-bucket.s3.ap-northeast-2.amazonaws.com/"
        dataNode[1]["imageUrl"].asText() shouldStartWith "https://test-bucket.s3.ap-northeast-2.amazonaws.com/"
    }

    @Test
    @WithMockUser(authorities = ["ROLE_SELLER"])
    fun `상품 이미지 삭제`() {
        val command = UploadImageCommand(
            "detail-1.png",
            MediaType.IMAGE_PNG_VALUE,
            "detail-1".length.toLong(),
            "detail-1".toByteArray().inputStream()
        )

        val imageUrl = s3Storage.upload(command)

        mockMvc.delete("/api/assets/products/images/delete?imageUrl=$imageUrl") {
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }
    }
}
