package com.zunza.ecommerce.application.auth.service

import com.zunza.ecommerce.application.account.provided.GetCustomerAccountUseCase
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.fixture.LoginCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerAuthenticationServiceTest {
    lateinit var tokenProvider: TokenProvider
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var tokenRepository: TokenRepository
    lateinit var getCustomerAccountUseCase: GetCustomerAccountUseCase
    lateinit var customerAuthentication: CustomerAuthenticationService

    val command = LoginCommandFixture.createLoginCommand()

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        getCustomerAccountUseCase = mockk()
        passwordEncoder = mockk()
        tokenRepository = mockk()
        customerAuthentication = CustomerAuthenticationService(tokenProvider, passwordEncoder, tokenRepository, getCustomerAccountUseCase)
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

        val result = customerAuthentication.login(command)

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

        shouldThrow<InvalidCredentialsException> { customerAuthentication.login(command) }

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

        shouldThrow<InvalidCredentialsException> { customerAuthentication.login(command) }

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

    @Test
    fun logout() {
        val accountId =1L
        val accessToken = "accessToken"
        val remainingTime = 1_000_000L

        val logoutCommand = LogoutCommand.of(accountId, accessToken)

        every { tokenProvider.getRemainingTime(any()) } returns remainingTime
        every { tokenRepository.addBlacklist(any(), any()) } returns Unit
        every { tokenRepository.removeToken(any()) } returns Unit

        customerAuthentication.logout(logoutCommand)

        verify(exactly = 1) {
            tokenProvider.getRemainingTime(accessToken)
            tokenRepository.addBlacklist(accessToken, remainingTime)
            tokenRepository.removeToken(accountId)
        }
    }

    @Test
    fun refresh() {
        val refreshToken = "refreshToken"
        val accountId = 1L
        val accountRole = "ROLE_CUSTOMER"
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"

        val account = mockk<Account> {
            every { id } returns accountId
            every { role.toString() } returns accountRole
        }

        every { tokenProvider.validateToken(any()) } returns Unit
        every { tokenProvider.getAccountId(any())} returns accountId
        every { getCustomerAccountUseCase.findByIdOrThrow(any()) } returns account
        every { tokenProvider.generateAccessToken(any(), any()) } returns newAccessToken
        every { tokenProvider.generateRefreshToken(any()) } returns newRefreshToken
        every { tokenRepository.save(any(), any()) } returns Unit

        customerAuthentication.refresh(refreshToken)

        verify(exactly = 1) {
            tokenProvider.validateToken(refreshToken)
            tokenProvider.getAccountId(refreshToken)
            getCustomerAccountUseCase.findByIdOrThrow(accountId)
            tokenProvider.generateAccessToken(accountId, accountRole)
            tokenProvider.generateRefreshToken(accountId)
            tokenRepository.save(accountId, newRefreshToken)
        }
    }
}