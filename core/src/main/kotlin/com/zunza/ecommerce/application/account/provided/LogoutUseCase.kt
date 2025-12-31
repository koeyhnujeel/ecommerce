package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.LogoutCommand

interface LogoutUseCase {
    fun logout(logoutCommand: LogoutCommand)
}