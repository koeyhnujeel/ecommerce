package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountCommandService
import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand
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

class ChangePasswordUseCaseTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var registerCustomerUseCase: RegisterCustomerUseCase
    lateinit var changePasswordUseCase: ChangePasswordUseCase

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        registerCustomerUseCase = mockk()
        changePasswordUseCase = AccountCommandService(emailSender, passwordEncoder, accountRepository, registerCustomerUseCase)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun changePassword() {
        val command = PasswordChangeCommand(1L, "newpassword1!")

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        changePasswordUseCase.changePassword(command)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(command.accountId)
            account.changePassword(command.newPassword, passwordEncoder )
            accountRepository.save(account)
        }
    }

    @Test
    fun changePasswordFailAccountNotFound() {
        val command = PasswordChangeCommand(1L, "newpassword1!")

        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { changePasswordUseCase.changePassword(command) }
    }
}