package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand

object AccountCommandFixture {
    fun createAccountRegisterCommand(
        email: String = "zunza@email.com",
        password: String = "verysecret1!",
        name: String = "홍길동",
        phone: String = "01012345678",
    ) = AccountRegisterCommand(email, password, name, phone)

    fun createChangePasswordCommand(
        accountId: Long = 1,
        newPassword: String = "password1!",
    ) = PasswordChangeCommand(accountId, newPassword)
}