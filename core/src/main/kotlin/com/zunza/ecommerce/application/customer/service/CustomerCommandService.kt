package com.zunza.ecommerce.application.customer.service

import com.zunza.ecommerce.application.customer.provided.*
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.*
import com.zunza.ecommerce.domain.customer.Customer
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerCommandService(
    private val customerRepository: CustomerRepository,
) : RegisterCustomerUseCase,
    RegisterShippingAddressUseCase,
    UpdateShippingAddressUseCase,
    DeleteShippingAddressUseCase,
    UpdateDefaultShippingAddressUseCase
{
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

    override fun updateShippingAddress(command: UpdateShippingAddressCommand) {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.updateShippingAddress(
            addressId = command.addressId,
            alias = command.alias,
            roadAddress = command.roadAddress,
            detailAddress = command.detailAddress,
            receiverName = command.receiverName,
            zipcode = command.zipcode,
            isDefault = command.isDefault
        )

        customerRepository.save(customer)
    }

    override fun deleteShippingAddress(command: DeleteShippingAddressCommand) {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.deleteShippingAddress(command.addressId)

        customerRepository.save(customer)
    }

    override fun updateDefaultShippingAddress(command: UpdateDefaultShippingAddressCommand) {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.updateShippingDefaultAddress(command.addressId)

        customerRepository.save(customer)
    }
}