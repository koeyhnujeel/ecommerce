package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.account.service.dto.result.LoginResult

interface LoginUseCase {
    fun login(loginCommand: LoginCommand): LoginResult
}