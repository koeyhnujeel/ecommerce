package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.account.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.account.service.dto.result.LoginResult
import com.zunza.ecommerce.application.account.service.dto.result.RefreshResult

interface AccountAuthenticator {
    fun login(loginCommand: LoginCommand): LoginResult

    fun logout(logoutCommand: LogoutCommand)

    fun refresh(refreshToken: String): RefreshResult
}