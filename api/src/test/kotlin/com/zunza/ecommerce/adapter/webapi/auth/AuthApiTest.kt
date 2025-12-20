package com.zunza.ecommerce.adapter.webapi.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.auth.dto.request.LoginRequest
import com.zunza.ecommerce.adapter.webapi.auth.dto.response.LoginResponse
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class AuthApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val accountRegister: AccountRegister,
    val tokenRepository: TokenRepository
) {
    @Test
    fun login() {
        accountRegister.registerCustomerAccount(
            AccountRegisterCommand("zunza@email.com", "password1!", "홍길동", "01012345678")
        )

        val request = LoginRequest("zunza@email.com", "password1!")

        val result = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.accountId") { value(1) }
            cookie {
                exists("accessToken")
                exists("refreshToken")
            }
        }.andReturn()

        val response: ApiResponse<LoginResponse> = objectMapper.readValue(result.response.contentAsString)

        val refreshToken = tokenRepository.findById(response.data!!.accountId)

        refreshToken shouldNotBe null
    }
}