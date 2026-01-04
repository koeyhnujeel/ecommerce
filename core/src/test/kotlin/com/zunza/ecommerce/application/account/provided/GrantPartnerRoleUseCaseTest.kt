package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.AccountCommandService
import com.zunza.ecommerce.application.customer.provided.RegisterCustomerUseCase
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.PasswordEncoder
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GrantPartnerRoleUseCaseTest {
    private lateinit var emailSender: EmailSender
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var accountRepository: AccountRepository
    private lateinit var registerCustomerUseCase: RegisterCustomerUseCase
    private lateinit var grantPartnerRoleUseCase: GrantPartnerRoleUseCase

    @BeforeEach
    fun setUp() {
        emailSender = mockk()
        passwordEncoder = mockk()
        accountRepository = mockk()
        registerCustomerUseCase = mockk()
        grantPartnerRoleUseCase = AccountCommandService(
            emailSender,
            passwordEncoder,
            accountRepository,
            registerCustomerUseCase
        )
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun grantPartnerRole() {
        val accountId = 1L
        val account = mockk<Account>(relaxed = true)

        every { accountRepository.findByIdOrThrow(accountId) } returns account
        every { accountRepository.save(any()) } returns mockk()

        grantPartnerRoleUseCase.grantPartnerRole(accountId)

        verify(exactly = 1) {
            accountRepository.findByIdOrThrow(accountId)
            account.grantPartnerRole()
            accountRepository.save(account)
        }
    }
}
