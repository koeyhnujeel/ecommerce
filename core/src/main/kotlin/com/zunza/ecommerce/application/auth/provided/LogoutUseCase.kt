package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand

interface LogoutUseCase {
    fun logout(logoutCommand: LogoutCommand)
}