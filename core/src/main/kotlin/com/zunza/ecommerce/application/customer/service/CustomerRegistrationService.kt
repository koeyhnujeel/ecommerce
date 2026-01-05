package com.zunza.ecommerce.application.customer.service

import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerRegistrationService(
    private val customerRepository: CustomerRepository,
) : CustomerRegister {
    override fun registerCustomer(registerCommand: CustomerRegisterCommand) {
        val customer = Customer.register(registerCommand.accountId, registerCommand.name, registerCommand.phone)

        customerRepository.save(customer)
    }

    override fun registerShippingAddress(command: RegisterShippingAddressCommand): Long {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.registerShippingAddress(
            alias = command.alias,
            roadAddress = command.roadAddress,
            detailAddress = command.detailAddress,
            receiverName = command.receiverName,
            zipcode = command.zipcode,
            isDefault = command.isDefault
        )

        return customerRepository.save(customer).id
    }
}