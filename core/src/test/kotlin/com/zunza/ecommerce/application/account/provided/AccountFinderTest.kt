package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.service.AccountFinderService
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AccountFinderTest {
    @MockK
    lateinit var accountRepository: AccountRepository

    @InjectMockKs
    lateinit var accountFinder: AccountFinderService

    @Test
    fun findByEmail() {
        val email = "zunza@email.com"

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByEmail(any()) } returns account

        accountFinder.findByEmail(email)

        verify(exactly = 1) {
            accountRepository.findByEmail(Email(email))
        }
    }

    @Test
    fun findByEmailReturnsNull() {
        val email = "zunza@email.com"

        every { accountRepository.findByEmail(any()) } returns null

        val account = accountFinder.findByEmail(email)

        account shouldBe null
    }

    @Test
    fun findByIdOrThrow() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrNull(any()) } returns account

        accountFinder.findByIdOrThrow(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }

    @Test
    fun findByIdOrThrowAccountNotFound() {
        val accountId = 1L

        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> {
            accountFinder.findByIdOrThrow(accountId)
        }

        verify(exactly = 1) {
            accountRepository.findByIdOrNull(accountId)
        }
    }
}