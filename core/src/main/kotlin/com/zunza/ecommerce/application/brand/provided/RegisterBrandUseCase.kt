package com.zunza.ecommerce.application.brand.provided

import com.zunza.ecommerce.application.brand.service.dto.command.RegisterBrandCommand

interface RegisterBrandUseCase {
    fun registerBrand(command: RegisterBrandCommand)
}