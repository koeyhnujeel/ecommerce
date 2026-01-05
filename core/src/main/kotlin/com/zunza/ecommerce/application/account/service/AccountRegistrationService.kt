package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import com.zunza.ecommerce.domain.shared.Email
import org.springframework.stereotype.Service

@Service
class AccountRegistrationService(
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
    private val customerRegister: CustomerRegister
) : AccountRegister {
    override fun registerCustomerAccount(registerCommand: AccountRegisterCommand): Long {
        checkDuplicateEmail(registerCommand)

        val account = Account.register(registerCommand.email, registerCommand.password, passwordEncoder)

        accountRepository.save(account)

        registerCustomer(account, registerCommand)

        sendAccountActivationLink(account)

        return account.id
    }

    private fun checkDuplicateEmail(registerCommand: AccountRegisterCommand) {
        if (accountRepository.existsByEmail(Email(registerCommand.email))) {
            throw DuplicateEmailException()
        }
    }

    private fun registerCustomer(
        account: Account,
        accountRegisterCommand: AccountRegisterCommand
    ) {
        val customerRegisterCommand = CustomerRegisterCommand(
            account.id,
            accountRegisterCommand.name,
            accountRegisterCommand.phone
        )

        customerRegister.registerCustomer(customerRegisterCommand)
    }

    private fun sendAccountActivationLink(account: Account) {
        emailSender.send(account.email.address, "등록을 완료해 주세요.", "아래 링크를 통해 등록을 완료해 주세요.")
    }
}