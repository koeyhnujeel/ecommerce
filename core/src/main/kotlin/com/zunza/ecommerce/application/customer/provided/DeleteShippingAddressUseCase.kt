package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand


interface DeleteShippingAddressUseCase {
    fun deleteShippingAddress(command: DeleteShippingAddressCommand)
}