package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.customer.provided.RegisterCustomerUseCase
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountCommandServiceTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var registerCustomerUseCase: RegisterCustomerUseCase
    lateinit var accountCommandService: AccountCommandService

    val registerCommand = AccountCommandFixture.createAccountRegisterCommand()

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        registerCustomerUseCase = mockk()
        accountCommandService = AccountCommandService(emailSender, passwordEncoder, accountRepository, registerCustomerUseCase)
    }

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
        every { registerCustomerUseCase.registerCustomer(any()) } returns Unit
        every { emailSender.send(any(), any(), any()) } returns Unit

        val result = accountCommandService.registerCustomerAccount(registerCommand)

        result shouldBe accountId

        verify(exactly = 1) {
            Account.register(registerCommand.email, registerCommand.password, passwordEncoder)
            accountRepository.existsByEmail(Email(registerCommand.email))
            accountRepository.save(mockAccount)
            registerCustomerUseCase.registerCustomer(any())
            emailSender.send(registerCommand.email, any(), any())
        }
    }

    @Test
    fun registerFailDuplicateEmail() {
        every { accountRepository.existsByEmail(any()) } returns true

        shouldThrow<DuplicateEmailException> { accountCommandService.registerCustomerAccount(registerCommand) }
    }

    @Test
    fun accountRegisterCommandFail() {
        shouldThrow<IllegalArgumentException> {
            accountCommandService.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(email = "zunza.com"))
        }

        shouldThrow<IllegalArgumentException> {
            accountCommandService.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(password = "invalid"))
        }

        shouldThrow<IllegalArgumentException> {
            accountCommandService.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(name = "이"))
        }

        shouldThrow<IllegalArgumentException> {
            accountCommandService.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(phone = "1012345678"))
        }
    }

    @Test
    fun activate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        accountCommandService.activateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.activate()
            accountRepository.save(account)
        }
    }

    @Test
    fun activateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountCommandService.activateCustomerAccount(1_000L) }
    }

    @Test
    fun deactivate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        accountCommandService.deactivateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.deactivate()
            accountRepository.save(account)
        }
    }

    @Test
    fun deactivateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountCommandService.deactivateCustomerAccount(1_000L) }
    }
}