package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.TokenProvider
import com.zunza.ecommerce.application.account.required.TokenRepository
import com.zunza.ecommerce.application.account.service.AccountAuthenticationService
import com.zunza.ecommerce.application.account.service.dto.command.LogoutCommand
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutUseCaseTest {
    lateinit var tokenProvider: TokenProvider
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var tokenRepository: TokenRepository
    lateinit var getCustomerAccountUseCase: GetCustomerAccountUseCase
    lateinit var logoutUseCase: LogoutUseCase

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        getCustomerAccountUseCase = mockk()
        passwordEncoder = mockk()
        tokenRepository = mockk()
        logoutUseCase = AccountAuthenticationService(tokenProvider, passwordEncoder, tokenRepository, getCustomerAccountUseCase)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
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

        logoutUseCase.logout(logoutCommand)

        verify(exactly = 1) {
            tokenProvider.getRemainingTime(accessToken)
            tokenRepository.addBlacklist(accessToken, remainingTime)
            tokenRepository.removeToken(accountId)
        }
    }
}