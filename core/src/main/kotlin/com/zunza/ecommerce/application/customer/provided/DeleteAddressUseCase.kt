package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.DeleteAddressCommand


interface DeleteAddressUseCase {
    fun deleteAddress(command: DeleteAddressCommand)
}