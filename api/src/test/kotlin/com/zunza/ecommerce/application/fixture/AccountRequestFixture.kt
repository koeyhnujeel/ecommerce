package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.account.service.dto.request.AccountRegisterRequest

object AccountRequestFixture {
    fun createAccountRegisterRequest(
        email: String = "zunza@email.com",
        password: String = "verysecret1!",
        name: String = "홍길동",
        phone: String = "01012345678",
    ) = AccountRegisterRequest(email, password, name, phone)
}