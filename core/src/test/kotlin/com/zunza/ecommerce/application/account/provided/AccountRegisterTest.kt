package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountRegisterService
import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class AccountRegisterTest {
    @MockK(relaxed = true)
    lateinit var emailSender: EmailSender

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @MockK
    lateinit var accountRepository: AccountRepository

    @MockK(relaxed = true)
    lateinit var customerRegister: CustomerRegister

    @InjectMockKs
    lateinit var accountRegister: AccountRegisterService

    val registerCommand = AccountCommandFixture.createAccountRegisterCommand()

    @Test
    fun register() {
        val accountId = 1L
        val accountEmail = Email(registerCommand.email)

        val mockAccount = mockk<Account> {
            every { id } returns accountId
            every { email } returns accountEmail
        }

        mockkObject(Account.Companion)

        every { Account.register(any(), any(), any()) } returns mockAccount
        every { accountRepository.existsByEmail(any()) } returns false
        every { accountRepository.save(any()) } returns mockAccount

        val result = accountRegister.registerCustomerAccount(registerCommand)

        result shouldBe accountId

        verify(exactly = 1) {
            Account.register(registerCommand.email, registerCommand.password, passwordEncoder)
            accountRepository.existsByEmail(Email(registerCommand.email))
            accountRepository.save(mockAccount)
            customerRegister.register(any())
            emailSender.send(registerCommand.email, any(), any())
        }
    }

    @Test
    fun registerFailDuplicateEmail() {
        every { accountRepository.existsByEmail(any()) } returns true

        shouldThrow<DuplicateEmailException> { accountRegister.registerCustomerAccount(registerCommand) }
    }

    @Test
    fun accountRegisterCommandFail() {
        shouldThrow<IllegalArgumentException> {
            accountRegister.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(email = "zunza.com"))
        }

        shouldThrow<IllegalArgumentException> {
            accountRegister.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(password = "invalid"))
        }

        shouldThrow<IllegalArgumentException> {
            accountRegister.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(name = "이"))
        }

        shouldThrow<IllegalArgumentException> {
            accountRegister.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(phone = "1012345678"))
        }
    }

    @Test
    fun activate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        accountRegister.activateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.activate()
            accountRepository.save(account)
        }
    }

    @Test
    fun activateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountRegister.activateCustomerAccount(1_000L) }
    }

    @Test
    fun deactivate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        accountRegister.deactivateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.deactivate()
            accountRepository.save(account)
        }
    }

    @Test
    fun deactivateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountRegister.deactivateCustomerAccount(1_000L) }
    }
}