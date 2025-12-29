package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.required.AccountRepository
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

class AccountQueryServiceTest {
    lateinit var accountRepository: AccountRepository
    lateinit var accountQueryService: AccountQueryService

    @BeforeEach
    fun setUp() {
        accountRepository = mockk()
        accountQueryService = AccountQueryService(accountRepository)
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

        accountQueryService.findByEmail(email)

        verify(exactly = 1) {
            accountRepository.findByEmail(Email(email))
        }
    }

    @Test
    fun findByEmailReturnsNull() {
        val email = "zunza@email.com"

        every { accountRepository.findByEmail(any()) } returns null

        val account = accountQueryService.findByEmail(email)

        account shouldBe null
    }

    @Test
    fun findByIdOrThrow() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrNull(any()) } returns account

        accountQueryService.findByIdOrThrow(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }

    @Test
    fun findByIdOrThrowAccountNotFound() {
        val accountId = 1L

        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> {
            accountQueryService.findByIdOrThrow(accountId)
        }

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }
}