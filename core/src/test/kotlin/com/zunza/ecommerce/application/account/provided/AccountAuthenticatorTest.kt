package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.TokenProvider
import com.zunza.ecommerce.application.account.required.TokenRepository
import com.zunza.ecommerce.application.account.service.AccountAuthenticationService
import com.zunza.ecommerce.application.account.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.fixture.LoginCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import com.zunza.ecommerce.domain.account.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountAuthenticatorTest {
    lateinit var tokenProvider: TokenProvider
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var tokenRepository: TokenRepository
    lateinit var accountFinder: AccountFinder
    lateinit var accountAuthenticator: AccountAuthenticator

    val command = LoginCommandFixture.createLoginCommand()

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        accountFinder = mockk()
        passwordEncoder = mockk()
        tokenRepository = mockk()

        accountAuthenticator = AccountAuthenticationService(
            tokenProvider,
            passwordEncoder,
            tokenRepository,
            accountFinder
        )
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun login() {
        val accountId = 1L
        val accountRoles = mutableSetOf(UserRole.ROLE_CUSTOMER)

        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        val account = mockk<Account>() {
            every { id } returns accountId
            every { roles } returns accountRoles
        }

        every { accountFinder.findByEmail(any()) } returns account
        every { account.login(any(), any()) } returns Unit
        every { tokenProvider.generateAccessToken(any(), any()) } returns accessToken
        every { tokenProvider.generateRefreshToken(any()) } returns refreshToken
        every { tokenRepository.save(any(), any()) } returns Unit

        val result = accountAuthenticator.login(command)

        result.accountId shouldBe 1L
        result.accessToken shouldBe accessToken
        result.refreshToken shouldBe refreshToken

        verify(exactly = 1) {
            accountFinder.findByEmail(command.email)
            account.login(command.password, passwordEncoder)
            tokenProvider.generateAccessToken(accountId, accountRoles.toList())
            tokenProvider.generateRefreshToken(accountId)
            tokenRepository.save(accountId, refreshToken)
        }
    }

    @Test
    fun loginFailInvalidEmail() {
        val account = mockk<Account>()

        every { accountFinder.findByEmail(any()) } returns null

        shouldThrow<InvalidCredentialsException> { accountAuthenticator.login(command) }

        verify(exactly = 1) {
            accountFinder.findByEmail(command.email)
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

        every { accountFinder.findByEmail(any()) } returns account
        every { account.login(any(), any()) } throws InvalidCredentialsException()

        shouldThrow<InvalidCredentialsException> { accountAuthenticator.login(command) }

        verify(exactly = 1) {
            accountFinder.findByEmail(command.email)
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

        accountAuthenticator.logout(logoutCommand)

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
        val accountRoles = mutableSetOf(UserRole.ROLE_CUSTOMER)
        val newAccessToken = "newAccessToken"
        val newRefreshToken = "newRefreshToken"

        val account = mockk<Account> {
            every { id } returns accountId
            every { roles } returns accountRoles
        }

        every { tokenProvider.validateToken(any()) } returns Unit
        every { tokenProvider.getAccountId(any())} returns accountId
        every { accountFinder.findByIdOrThrow(any()) } returns account
        every { tokenProvider.generateAccessToken(any(), any()) } returns newAccessToken
        every { tokenProvider.generateRefreshToken(any()) } returns newRefreshToken
        every { tokenRepository.save(any(), any()) } returns Unit

        accountAuthenticator.refresh(refreshToken)

        verify(exactly = 1) {
            tokenProvider.validateToken(refreshToken)
            tokenProvider.getAccountId(refreshToken)
            accountFinder.findByIdOrThrow(accountId)
            tokenProvider.generateAccessToken(accountId, accountRoles.toList())
            tokenProvider.generateRefreshToken(accountId)
            tokenRepository.save(accountId, newRefreshToken)
        }
    }
}