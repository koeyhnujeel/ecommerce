package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.UpdateShippingAddressCommand


interface UpdateShippingAddressUseCase {
    fun updateShippingAddress(command: UpdateShippingAddressCommand)
}