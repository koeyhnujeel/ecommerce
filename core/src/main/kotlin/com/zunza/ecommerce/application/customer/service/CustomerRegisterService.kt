package com.zunza.ecommerce.application.customer.service

import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerRegisterService(
    private val customerRepository: CustomerRepository,
) : CustomerRegister {
    override fun register(registerCommand: CustomerRegisterCommand) {
        val customer = Customer.register(registerCommand.accountId, registerCommand.name, registerCommand.phone)

        customerRepository.save(customer)
    }
}