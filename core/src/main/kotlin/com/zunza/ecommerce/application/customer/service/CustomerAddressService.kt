package com.zunza.ecommerce.application.customer.service

import com.zunza.ecommerce.application.customer.provided.DeleteAddressUseCase
import com.zunza.ecommerce.application.customer.provided.RegisterAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateAddressUseCase
import com.zunza.ecommerce.application.customer.provided.UpdateDefaultAddressUseCase
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.RegisterAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultAddressCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerAddressService(
    private val customerRepository: CustomerRepository
) : RegisterAddressUseCase,
    UpdateAddressUseCase,
    DeleteAddressUseCase,
    UpdateDefaultAddressUseCase {
    override fun registerAddress(command: RegisterAddressCommand): Long {
        val customer = customerRepository.findWithAddressesOrThrow(command.accountId)

        customer.registerAddress(
            alias = command.alias,
            roadAddress = command.roadAddress,
            detailAddress = command.detailAddress,
            receiverName = command.receiverName,
            zipcode = command.zipcode,
            isDefault = command.isDefault
        )

        return customerRepository.save(customer).id
    }

    override fun updateAddress(command: UpdateAddressCommand) {
        val customer = customerRepository.findWithAddressesOrThrow(command.accountId)

        customer.updateAddress(
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

    override fun deleteAddress(command: DeleteAddressCommand) {
        val customer = customerRepository.findWithAddressesOrThrow(command.accountId)

        customer.deleteAddress(command.addressId)

        customerRepository.save(customer)
    }

    override fun updateDefaultAddress(command: UpdateDefaultAddressCommand) {
        val customer = customerRepository.findWithAddressesOrThrow(command.accountId)

        customer.updateDefaultAddress(command.addressId)

        customerRepository.save(customer)
    }
}