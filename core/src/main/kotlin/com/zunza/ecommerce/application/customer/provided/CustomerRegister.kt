package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand

interface CustomerRegister {
    fun register(registerCommand: CustomerRegisterCommand)
}