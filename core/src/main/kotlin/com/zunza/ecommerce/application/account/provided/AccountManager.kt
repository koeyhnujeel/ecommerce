package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand

interface AccountManager {
    fun changePassword(changeCommand: PasswordChangeCommand)
}