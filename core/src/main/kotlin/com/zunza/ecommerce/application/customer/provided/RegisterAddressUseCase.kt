package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.RegisterAddressCommand

interface RegisterAddressUseCase {
    fun registerAddress(command: RegisterAddressCommand): Long
}