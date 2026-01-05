package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand
import com.zunza.ecommerce.domain.account.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountManagementService(
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
) : AccountManager {
    override fun activateCustomerAccount(accountId: Long) {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.activate()
    }

    override fun deactivateCustomerAccount(accountId: Long) {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.deactivate()
    }

    override fun changePassword(changeCommand: PasswordChangeCommand) {
        val account = accountRepository.findByIdOrThrow(changeCommand.accountId)

        account.changePassword(changeCommand.newPassword, passwordEncoder)
    }

    override fun grantPartnerRole(accountId: Long) {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.grantPartnerRole()
    }
}