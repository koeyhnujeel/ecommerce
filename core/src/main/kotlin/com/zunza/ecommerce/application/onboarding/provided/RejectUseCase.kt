package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.service.dto.command.RejectCommand

interface RejectUseCase {
    fun reject(command: RejectCommand)
}