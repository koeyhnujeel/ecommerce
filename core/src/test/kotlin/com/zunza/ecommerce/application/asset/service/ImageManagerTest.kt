package com.zunza.ecommerce.application.asset.service

import com.zunza.ecommerce.application.asset.provided.ImageManager
import com.zunza.ecommerce.application.asset.required.ImageStorage
import com.zunza.ecommerce.application.asset.service.dto.command.UploadImageCommand
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

class ImageManagerTest {
    private lateinit var imageStorage: ImageStorage
    private lateinit var imageManager: ImageManager

    @BeforeEach
    fun setUp() {
        imageStorage = mockk()
        imageManager = ImageManagementService(imageStorage)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun `상품 대표 이미지를 업로드 한다`() {
        val command = createUploadImageCommand("main.png")
        val expectedUrl = "https://example.com/main.png"

        every { imageStorage.upload(command) } returns expectedUrl

        val actual = imageManager.uploadProductMainImage(command)

        actual shouldBe expectedUrl
        verify(exactly = 1) {
            imageStorage.upload(command)
        }
    }

    @Test
    fun `상품 상세 이미지들을 업로드 한다`() {
        val commands = listOf(
            createUploadImageCommand("detail-1.png"),
            createUploadImageCommand("detail-2.png")
        )
        val expectedUrls = listOf(
            "https://example.com/detail-1.png",
            "https://example.com/detail-2.png"
        )

        every { imageStorage.uploadAll(commands) } returns expectedUrls

        val actual = imageManager.uploadProductDetailImages(commands)

        actual shouldBe expectedUrls
        verify(exactly = 1) {
            imageStorage.uploadAll(commands)
        }
    }

    private fun createUploadImageCommand(originalFilename: String): UploadImageCommand {
        return UploadImageCommand(
            originalFilename = originalFilename,
            contentType = "image/png",
            contentLength = 1L,
            imageInputStream = ByteArrayInputStream(ByteArray(0))
        )
    }
}
