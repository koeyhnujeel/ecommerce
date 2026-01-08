package com.zunza.ecommerce.adapter.webapi.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.webapi.auth.dto.request.LoginRequest
import com.zunza.ecommerce.adapter.webapi.auth.dto.response.LoginResponse
import com.zunza.ecommerce.application.account.provided.AccountAuthenticator
import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.required.TokenRepository
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class AuthApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val accountManager: AccountManager,
    val tokenRepository: TokenRepository,
    val accountRegister: AccountRegister,
    val accountAuthenticator: AccountAuthenticator,
) {
    var accountId: Long = 0
    lateinit var accessToken: String
    lateinit var refreshToken: String

    @BeforeEach
    fun setUp() {
        val registerCommand = AccountRegisterCommand(
            email = "example@email.com",
            password = "password1!",
            name = "이순신",
            phone = "01022225678",
        )

        accountId = accountRegister.registerCustomerAccount(registerCommand)

        accountManager.activateCustomerAccount(accountId)

        val loginCommand = LoginCommand(registerCommand.email, registerCommand.password)

        val loginResult = accountAuthenticator.login(loginCommand)

        accessToken = loginResult.accessToken
        refreshToken = loginResult.refreshToken
    }

    @Test
    fun login() {
        val command = AccountRegisterCommand("zunza@email.com", "password1!", "홍길동", "01012345678")

        val accountId = accountRegister.registerCustomerAccount(command)
        accountManager.activateCustomerAccount(accountId)

        val request = LoginRequest(command.email, command.password)

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
        mockMvc.post("/api/auth/logout") {
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
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

        tokenRepository.findById(accountId) shouldBe null
        tokenRepository.isBlacklisted(accessToken) shouldBe true
    }

    @Test
    fun refresh() {
        val result = mockMvc.post("/api/auth/refresh") {
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.accountId") { accountId }
            jsonPath("$.timestamp") { exists() }
            cookie {
                exists("accessToken")
                exists("refreshToken")
            }
        }.andReturn()

        val newAccessToken = result.response.cookies
            .first { it.name == "accessToken" }
            .value

        val newRefreshToken = result.response.cookies
            .first { it.name == "refreshToken" }
            .value

        newAccessToken shouldNotBe accessToken
        newRefreshToken shouldNotBe refreshToken

        val found = tokenRepository.findById(accountId)

        found shouldBe newRefreshToken
    }
}