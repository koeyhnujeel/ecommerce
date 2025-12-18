package com.zunza.ecommerce.adapter.webapi.account.fixture

import com.zunza.ecommerce.adapter.webapi.account.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand

object AccountRequestFixture {
    fun createAccountRegisterRequest(
        email: String = "zunza@email.com",
        password: String = "verysecret1!",
        name: String = "홍길동",
        phone: String = "01012345678",
    ) = AccountRegisterRequest(email, password, name, phone)

    fun createAccountRegisterCommand(
        email: String = "zunza@email.com",
        password: String = "verysecret1!",
        name: String = "홍길동",
        phone: String = "01012345678",
    ) = AccountRegisterCommand(email, password, name, phone)
}