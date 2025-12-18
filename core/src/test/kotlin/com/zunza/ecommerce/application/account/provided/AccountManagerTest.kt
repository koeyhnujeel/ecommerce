package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountManagerService
import com.zunza.ecommerce.application.fixture.AccountCommandFixture
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class AccountManagerTest {
    lateinit var passwordEncoder: PasswordEncoder
    lateinit var accountRepository: AccountRepository
    lateinit var accountManager: AccountManager

    @BeforeEach
    fun setUp() {
        passwordEncoder = mockk<PasswordEncoder>()
        accountRepository = mockk<AccountRepository>()

        accountManager = AccountManagerService(passwordEncoder, accountRepository)
    }

    @Test
    fun changePassword() {
        val command = AccountCommandFixture.createChangePasswordCommand()

        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(any()) } returns account
        every { accountRepository.save(any()) } returns account

        accountManager.changePassword(command)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(command.accountId)
            account.changePassword(command.newPassword, passwordEncoder)
            accountRepository.save(account)
        }
    }

    @Test
    fun changePasswordRequestFail() {
        shouldThrow<IllegalArgumentException> { AccountCommandFixture.createChangePasswordCommand(newPassword = "invalid") }
    }
}