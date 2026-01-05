package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateShippingAddressCommand

interface CustomerManager {
    fun updateShippingAddress(command: UpdateShippingAddressCommand)

    fun deleteShippingAddress(command: DeleteShippingAddressCommand)

    fun updateDefaultShippingAddress(command: UpdateDefaultShippingAddressCommand)
}