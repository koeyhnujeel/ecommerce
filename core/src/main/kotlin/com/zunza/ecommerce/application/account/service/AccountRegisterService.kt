package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import com.zunza.ecommerce.domain.account.Email
import com.zunza.ecommerce.domain.account.PasswordEncoder
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountRegisterService(
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
    private val customerRepository: CustomerRepository,
) : AccountRegister {
    override fun registerCustomerAccount(registerCommand: AccountRegisterCommand): Long {
        checkDuplicateEmail(registerCommand)

        val account = Account.register(registerCommand.email, registerCommand.password, passwordEncoder)

        accountRepository.save(account)

        val customer = Customer.register(account.id, registerCommand.name, registerCommand.phone)

        customerRepository.save(customer)

        sendAccountActivationLink(account)

        return account.id
    }

    override fun activateCustomerAccount(accountId: Long) {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.activate()

        accountRepository.save(account)
    }

    override fun deactivateCustomerAccount(accountId: Long) {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.deactivate()

        accountRepository.save(account)
    }

    private fun checkDuplicateEmail(registerCommand: AccountRegisterCommand) {
        if (accountRepository.existsByEmail(Email(registerCommand.email))) {
            throw DuplicateEmailException()
        }
    }

    private fun sendAccountActivationLink(account: Account) {
        emailSender.send(account.email.address, "등록을 완료해 주세요.", "아래 링크를 통해 등록을 완료해 주세요.")
    }
}