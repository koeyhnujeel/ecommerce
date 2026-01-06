package com.zunza.ecommerce.application.product.provided

import com.zunza.ecommerce.application.product.service.dto.command.RegisterProductCommand

interface ProductRegister {
    fun register(command: RegisterProductCommand): Long
}