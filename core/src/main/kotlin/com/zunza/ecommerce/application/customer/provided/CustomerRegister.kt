package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand

interface CustomerRegister {
    fun registerCustomer(registerCommand: CustomerRegisterCommand)

    fun registerShippingAddress(command: RegisterShippingAddressCommand): Long
}