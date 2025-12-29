package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.auth.service.dto.result.LoginResult

interface LoginUseCase {
    fun login(loginCommand: LoginCommand): LoginResult
}