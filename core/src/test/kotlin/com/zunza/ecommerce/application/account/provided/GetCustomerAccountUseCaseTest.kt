package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.service.AccountQueryService
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetCustomerAccountUseCaseTest {
    lateinit var accountRepository: AccountRepository
    lateinit var getCustomerAccountUseCase: GetCustomerAccountUseCase

    @BeforeEach
    fun setUp() {
        accountRepository = mockk()
        getCustomerAccountUseCase = AccountQueryService(accountRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun findByEmail() {
        val email = "zunza@email.com"

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByEmail(any()) } returns account

        getCustomerAccountUseCase.findByEmail(email)

        verify(exactly = 1) {
            accountRepository.findByEmail(Email(email))
        }
    }

    @Test
    fun findByEmailReturnsNull() {
        val email = "zunza@email.com"

        every { accountRepository.findByEmail(any()) } returns null

        val account = getCustomerAccountUseCase.findByEmail(email)

        account shouldBe null
    }

    @Test
    fun findByIdOrThrow() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrNull(any()) } returns account

        getCustomerAccountUseCase.findByIdOrThrow(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }

    @Test
    fun findByIdOrThrowAccountNotFound() {
        val accountId = 1L

        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> {
            getCustomerAccountUseCase.findByIdOrThrow(accountId)
        }

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }
}