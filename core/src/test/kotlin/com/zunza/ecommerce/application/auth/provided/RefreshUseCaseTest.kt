package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.account.provided.GetCustomerAccountUseCase
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.CustomerAuthenticationService
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RefreshUseCaseTest {
    lateinit var tokenProvider: TokenProvider
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var tokenRepository: TokenRepository
    lateinit var getCustomerAccountUseCase: GetCustomerAccountUseCase
    lateinit var refreshUseCase: RefreshUseCase

    @BeforeEach
    fun setUp() {
        tokenProvider = mockk()
        getCustomerAccountUseCase = mockk()
        passwordEncoder = mockk()
        tokenRepository = mockk()
        refreshUseCase = CustomerAuthenticationService(tokenProvider, passwordEncoder, tokenRepository, getCustomerAccountUseCase)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
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

        refreshUseCase.refresh(refreshToken)

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