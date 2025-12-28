package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultAddressCommand

interface UpdateDefaultAddressUseCase {
    fun updateDefaultAddress(command: UpdateDefaultAddressCommand)
}