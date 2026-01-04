package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand

interface UpdateDefaultShippingAddressUseCase {
    fun updateDefaultShippingAddress(command: UpdateDefaultShippingAddressCommand)
}