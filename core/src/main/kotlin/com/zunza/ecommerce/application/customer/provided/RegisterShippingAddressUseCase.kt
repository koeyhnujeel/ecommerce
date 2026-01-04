package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand

interface RegisterShippingAddressUseCase {
    fun registerShippingAddress(command: RegisterShippingAddressCommand): Long
}