package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountCommandService
import com.zunza.ecommerce.application.customer.provided.RegisterCustomerUseCase
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ActivateCustomerAccountUseCaseTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var registerCustomerUseCase: RegisterCustomerUseCase
    lateinit var activateCustomerAccountUseCase: ActivateCustomerAccountUseCase

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        registerCustomerUseCase = mockk()
        activateCustomerAccountUseCase = AccountCommandService(emailSender, passwordEncoder, accountRepository, registerCustomerUseCase)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun activate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        activateCustomerAccountUseCase.activateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.activate()
            accountRepository.save(account)
        }
    }

    @Test
    fun activateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { activateCustomerAccountUseCase.activateCustomerAccount(1_000L) }
    }
}