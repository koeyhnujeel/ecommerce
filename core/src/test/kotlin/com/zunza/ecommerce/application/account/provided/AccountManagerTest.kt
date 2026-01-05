package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountManagementService
import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand
import com.zunza.ecommerce.application.customer.provided.CustomerRegister
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

class AccountManagerTest {
    lateinit var emailSender: EmailSender
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var customerRegister: CustomerRegister
    lateinit var accountManager: AccountManager

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        customerRegister = mockk()

        accountManager = AccountManagementService(passwordEncoder, accountRepository)
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

        accountManager.activateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.activate()
        }
    }

    @Test
    fun activateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountManager.activateCustomerAccount(1_000L) }
    }

    @Test
    fun deactivate() {
        val accountId = 1L

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account

        accountManager.deactivateCustomerAccount(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.deactivate()
        }
    }

    @Test
    fun deactivateFailAccountNotFound() {
        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountManager.deactivateCustomerAccount(1_000L) }
    }

    @Test
    fun changePassword() {
        val command = PasswordChangeCommand(1L, "newpassword1!")

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account

        accountManager.changePassword(command)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(command.accountId)
            account.changePassword(command.newPassword, passwordEncoder )
        }
    }

    @Test
    fun changePasswordFailAccountNotFound() {
        val command = PasswordChangeCommand(1L, "newpassword1!")

        every { accountRepository.findByIdOrNull(any()) } returns null

        shouldThrow<AccountNotFoundException> { accountManager.changePassword(command) }
    }

    @Test
    fun grantPartnerRole() {
        val accountId = 1L
        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(accountId) } returns account

        accountManager.grantPartnerRole(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.grantPartnerRole()
        }
    }
}