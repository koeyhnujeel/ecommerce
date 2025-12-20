package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.account.provided.AccountFinder
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.CustomerAuthenticationService
import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.fixture.LoginCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CustomerAuthenticationTest {
    @MockK
    lateinit var tokenProvider: TokenProvider

    @MockK
    lateinit var accountFinder: AccountFinder

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @MockK
    lateinit var tokenRepository: TokenRepository

    @InjectMockKs
    lateinit var customerAuthentication: CustomerAuthenticationService

    val command = LoginCommandFixture.createLoginCommand()

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

        every { accountFinder.findByEmail(any()) } returns account
        every { account.login(any(), any()) } just Runs
        every { tokenProvider.generateAccessToken(any(), any()) } returns accessToken
        every { tokenProvider.generateRefreshToken(any()) } returns refreshToken
        every { tokenRepository.save(any(), any()) } just Runs

        val result = customerAuthentication.login(command)

        result.accountId shouldBe 1L
        result.accessToken shouldBe accessToken
        result.refreshToken shouldBe refreshToken

        verify(exactly = 1) {
            accountFinder.findByEmail(command.email)
            account.login(command.password, passwordEncoder)
            tokenProvider.generateAccessToken(accountId, accountRole)
            tokenProvider.generateRefreshToken(accountId)
            tokenRepository.save(accountId, refreshToken)
        }
    }

    @Test
    fun loginFailInvalidEmail() {
        val account = mockk<Account>()

        every { accountFinder.findByEmail(any()) } returns null

        shouldThrow<InvalidCredentialsException> { customerAuthentication.login(command) }

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

        shouldThrow<InvalidCredentialsException> { customerAuthentication.login(command) }

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
        
        customerAuthentication.logout(logoutCommand)

        verify(exactly = 1) {
            tokenProvider.getRemainingTime(accessToken)
            tokenRepository.addBlacklist(accessToken, remainingTime)
            tokenRepository.removeToken(accountId)
        }
    }
}