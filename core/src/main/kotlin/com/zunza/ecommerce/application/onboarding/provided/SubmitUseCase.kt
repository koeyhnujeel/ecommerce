package com.zunza.ecommerce.application.onboarding.provided

import com.zunza.ecommerce.application.onboarding.service.dto.command.SubmitCommand
import com.zunza.ecommerce.application.onboarding.service.dto.result.SubmitResult

interface SubmitUseCase {
    fun submit(command: SubmitCommand): SubmitResult
}