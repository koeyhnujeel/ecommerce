package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountCommandService
import com.zunza.ecommerce.application.customer.provided.RegisterCustomerUseCase
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import com.zunza.ecommerce.domain.account.Email
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterCustomerAccountUseCaseTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var registerCustomerUseCase: RegisterCustomerUseCase
    lateinit var registerCustomerAccountUseCase: RegisterCustomerAccountUseCase

    val registerCommand = AccountCommandFixture.createAccountRegisterCommand()

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        registerCustomerUseCase = mockk()
        registerCustomerAccountUseCase = AccountCommandService(emailSender, passwordEncoder, accountRepository, registerCustomerUseCase)
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
        every { registerCustomerUseCase.registerCustomer(any()) } returns Unit
        every { emailSender.send(any(), any(), any()) } returns Unit

        val result = registerCustomerAccountUseCase.registerCustomerAccount(registerCommand)

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

        shouldThrow<DuplicateEmailException> { registerCustomerAccountUseCase.registerCustomerAccount(registerCommand) }
    }

    @Test
    fun accountRegisterCommandFail() {
        shouldThrow<IllegalArgumentException> {
            registerCustomerAccountUseCase.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(email = "zunza.com"))
        }

        shouldThrow<IllegalArgumentException> {
            registerCustomerAccountUseCase.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(password = "invalid"))
        }

        shouldThrow<IllegalArgumentException> {
            registerCustomerAccountUseCase.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(name = "이"))
        }

        shouldThrow<IllegalArgumentException> {
            registerCustomerAccountUseCase.registerCustomerAccount(AccountCommandFixture.createAccountRegisterCommand(phone = "1012345678"))
        }
    }
}