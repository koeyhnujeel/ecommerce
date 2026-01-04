package com.zunza.ecommerce.application.partner.provided

import com.zunza.ecommerce.application.partner.service.dto.command.RegisterPartnerCommand

interface RegisterPartnerUseCase {
    fun registerPartner(command: RegisterPartnerCommand): Long
}