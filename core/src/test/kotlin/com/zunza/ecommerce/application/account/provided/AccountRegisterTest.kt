package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountRegisterService
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.*
import com.zunza.ecommerce.domain.customer.Customer
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AccountRegisterTest {
    val registerCommand = AccountCommandFixture.createAccountRegisterCommand()

    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var customerRepository: CustomerRepository
    lateinit var accountRegister: AccountRegisterService

    @BeforeEach
    fun setUp() {
        emailSender = mockk<EmailSender>(relaxed = true)
        passwordEncoder = mockk<PasswordEncoder>()
        accountRepository = mockk<AccountRepository>()
        customerRepository = mockk<CustomerRepository>()

        accountRegister = AccountRegisterService(emailSender, passwordEncoder, accountRepository, customerRepository)
    }

    @Test
    fun register() {
        val accountId = 1L
        val accountEmail = Email(registerCommand.email)

        val mockAccount = mockk<Account> {
            every { id } returns accountId
            every { email } returns accountEmail
        }

        val mockCustomer = mockk<Customer>()

        mockkObject(Account.Companion)
        mockkObject(Customer.Companion)

        every { Account.register(any(), any(), any()) } returns mockAccount
        every { Customer.register(any(), any(), any()) } returns mockCustomer
        every { accountRepository.existsByEmail(any()) } returns false
        every { accountRepository.save(any()) } returns mockAccount
        every { customerRepository.save(any()) } returns mockCustomer

        val result = accountRegister.registerCustomerAccount(registerCommand)

        result shouldBe accountId

        verify(exactly = 1) {
            Account.register(registerCommand.email, registerCommand.password, passwordEncoder)
            Customer.register(accountId, registerCommand.name, registerCommand.phone)
            accountRepository.existsByEmail(Email(registerCommand.email))
            accountRepository.save(mockAccount)
            customerRepository.save(mockCustomer)
            emailSender.send(registerCommand.email, any(), any())
        }
    }

    @Test
    fun registerFailDuplicateEmail() {
        every { accountRepository.existsByEmail(any()) } returns true

        shouldThrow<DuplicateEmailException> { accountRegister.registerCustomerAccount(registerCommand) }
    }

    @Test
    fun customerRegisterRequestFail() {
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