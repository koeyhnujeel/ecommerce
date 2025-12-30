package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.account.provided.GetCustomerAccountUseCase
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.CustomerAuthenticationService
import com.zunza.ecommerce.application.fixture.LoginCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginUseCaseTest {
    lateinit var tokenProvider: TokenProvider
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var tokenRepository: TokenRepository
    lateinit var getCustomerAccountUseCase: GetCustomerAccountUseCase
    lateinit var loginUseCase: LoginUseCase

    val command = LoginCommandFixture.createLoginCommand()

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        getCustomerAccountUseCase = mockk()
        passwordEncoder = mockk()
        tokenRepository = mockk()
        loginUseCase = CustomerAuthenticationService(tokenProvider, passwordEncoder, tokenRepository, getCustomerAccountUseCase)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun login() {
        val accountId = 1L
        val accountRole = "ROLE_CUSTOMER"

        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        val account = mockk<Account>() {
            every { id } returns accountId
            every { role.toString() } returns accountRole
        }

        every { getCustomerAccountUseCase.findByEmail(any()) } returns account
        every { account.login(any(), any()) } just Runs
        every { tokenProvider.generateAccessToken(any(), any()) } returns accessToken
        every { tokenProvider.generateRefreshToken(any()) } returns refreshToken
        every { tokenRepository.save(any(), any()) } just Runs

        val result = loginUseCase.login(command)

        result.accountId shouldBe 1L
        result.accessToken shouldBe accessToken
        result.refreshToken shouldBe refreshToken

        verify(exactly = 1) {
            getCustomerAccountUseCase.findByEmail(command.email)
            account.login(command.password, passwordEncoder)
            tokenProvider.generateAccessToken(accountId, accountRole)
            tokenProvider.generateRefreshToken(accountId)
            tokenRepository.save(accountId, refreshToken)
        }
    }

    @Test
    fun loginFailInvalidEmail() {
        val account = mockk<Account>()

        every { getCustomerAccountUseCase.findByEmail(any()) } returns null

        shouldThrow<InvalidCredentialsException> { loginUseCase.login(command) }

        verify(exactly = 1) {
            getCustomerAccountUseCase.findByEmail(command.email)
        }

        verify(exactly = 0) {
            account.login(any(), any())
            tokenProvider.generateAccessToken(any(), any())
            tokenProvider.generateRefreshToken(any())
            tokenRepository.save(any(), any())
        }
    }

    @Test
    fun loginFailInvalidPassword() {
        val account = mockk<Account>()

        every { getCustomerAccountUseCase.findByEmail(any()) } returns account
        every { account.login(any(), any()) } throws InvalidCredentialsException()

        shouldThrow<InvalidCredentialsException> { loginUseCase.login(command) }

        verify(exactly = 1) {
            getCustomerAccountUseCase.findByEmail(command.email)
            account.login(command.password, passwordEncoder)
        }

        verify(exactly = 0) {
            tokenProvider.generateAccessToken(any(), any())
            tokenProvider.generateRefreshToken(any())
            tokenRepository.save(any(), any())
        }
    }
}