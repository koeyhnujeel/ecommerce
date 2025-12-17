package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.EmailSender
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.application.account.service.dto.response.AccountActivateResponse
import com.zunza.ecommerce.application.account.service.dto.response.AccountDeactivateResponse
import com.zunza.ecommerce.application.account.service.dto.response.AccountRegisterResponse
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.domain.account.Account
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import com.zunza.ecommerce.domain.account.Email
import com.zunza.ecommerce.domain.account.PasswordEncoder
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional
class AccountRegisterService(
    private val emailSender: EmailSender,
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
    private val customerRepository: CustomerRepository,
) : AccountRegister {
    override fun registerCustomerAccount(registerRequest: AccountRegisterRequest): AccountRegisterResponse {
        checkDuplicateEmail(registerRequest)

        val account = Account.register(registerRequest.email, registerRequest.password, passwordEncoder)

        accountRepository.save(account)

        val customer = Customer.register(account.id, registerRequest.name, registerRequest.phone)

        customerRepository.save(customer)

        sendAccountActivationLink(account)

        return AccountRegisterResponse.from(account)
    }

    override fun activateCustomerAccount(accountId: Long): AccountActivateResponse {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.activate()

        accountRepository.save(account)

        return AccountActivateResponse.from(account)
    }

    override fun deactivateCustomerAccount(accountId: Long): AccountDeactivateResponse {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.deactivate()

        accountRepository.save(account)

        return AccountDeactivateResponse.from(account)
    }

    private fun checkDuplicateEmail(registerRequest: AccountRegisterRequest) {
        if (accountRepository.existsByEmail(Email(registerRequest.email))) {
            throw DuplicateEmailException()
        }
    }

    private fun sendAccountActivationLink(account: Account) {
        emailSender.send(account.email.address, "등록을 완료해 주세요.", "아래 링크를 통해 등록을 완료해 주세요.")
    }
}