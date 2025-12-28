package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.UpdateAddressCommand


interface UpdateAddressUseCase {
    fun updateAddress(command: UpdateAddressCommand)
}