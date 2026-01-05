package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.service.AccountRegistrationService
import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import com.zunza.ecommerce.domain.shared.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountRegisterTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var customerRegister: CustomerRegister
    lateinit var accountRegister: AccountRegister

    val registerCommand = AccountCommandFixture.createAccountRegisterCommand()

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        customerRegister = mockk()

        accountRegister = AccountRegistrationService(
            emailSender,
            passwordEncoder,
            accountRepository,
            customerRegister
        )
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
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
        every { customerRegister.registerCustomer(any()) } returns Unit
        every { emailSender.send(any(), any(), any()) } returns Unit

        val result = accountRegister.registerCustomerAccount(registerCommand)

        result shouldBe accountId

        verify(exactly = 1) {
            Account.register(registerCommand.email, registerCommand.password, passwordEncoder)
            accountRepository.existsByEmail(Email(registerCommand.email))
            accountRepository.save(mockAccount)
            customerRegister.registerCustomer(any())
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
}