package com.zunza.ecommerce.adapter.webapi.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.auth.dto.request.LoginRequest
import com.zunza.ecommerce.adapter.webapi.auth.dto.response.LoginResponse
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.auth.provided.CustomerAuthentication
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.servlet.http.Cookie
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
    val tokenRepository: TokenRepository,
    val tokenProvider: TokenProvider,
    val customerAuthentication: CustomerAuthentication
) {
    @Test
    fun login() {
        val registerCommand = AccountRegisterCommand("zunza@email.com", "password1!", "홍길동", "01012345678")

        accountRegister.registerCustomerAccount(registerCommand)

        val request = LoginRequest("zunza@email.com", "password1!")

        val result = mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.accountId") { exists() }
            cookie {
                exists("accessToken")
                exists("refreshToken")
            }
        }.andReturn()

        val response: ApiResponse<LoginResponse> = objectMapper.readValue(result.response.contentAsString)

        val refreshToken = tokenRepository.findById(response.data!!.accountId)

        refreshToken shouldNotBe null
    }

    @Test
    fun logout() {
        val registerCommand = AccountRegisterCommand("zunza11@email.com", "password11!", "홍길동", "01012345670")
        accountRegister.registerCustomerAccount(registerCommand)

        val loginCommand = LoginCommand(registerCommand.email, registerCommand.password)
        val loginResult = customerAuthentication.login(loginCommand)

        mockMvc.post("/api/auth/logout") {
            cookie(
                Cookie("accessToken", loginResult.accessToken),
                Cookie("refreshToken", loginResult.refreshToken)
                )
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
            cookie {
                maxAge("accessToken", 0)
                maxAge("refreshToken", 0)
            }
        }

        tokenRepository.findById(loginResult.accountId) shouldBe null
        tokenRepository.isBlacklisted(loginResult.accessToken) shouldBe true
    }
}