package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.auth.service.dto.result.LoginResult

interface CustomerAuthentication {
    fun login(loginCommand: LoginCommand): LoginResult

    fun logout(logoutCommand: LogoutCommand)
}