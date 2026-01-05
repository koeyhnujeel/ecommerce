package com.zunza.ecommerce.application.customer.service

import com.zunza.ecommerce.application.customer.provided.CustomerManager
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateShippingAddressCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerManagementService(
    private val customerRepository: CustomerRepository,
) : CustomerManager {
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
    }

    override fun deleteShippingAddress(command: DeleteShippingAddressCommand) {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.deleteShippingAddress(command.addressId)
    }

    override fun updateDefaultShippingAddress(command: UpdateDefaultShippingAddressCommand) {
        val customer = customerRepository.findWithShippingAddressesOrThrow(command.accountId)

        customer.updateShippingDefaultAddress(command.addressId)
    }
}