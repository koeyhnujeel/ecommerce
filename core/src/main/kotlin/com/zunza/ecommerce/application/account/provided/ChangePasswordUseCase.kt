package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand

interface ChangePasswordUseCase {
    fun changePassword(changeCommand: PasswordChangeCommand)
}