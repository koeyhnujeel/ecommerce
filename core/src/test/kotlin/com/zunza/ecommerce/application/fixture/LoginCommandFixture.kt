package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand

object LoginCommandFixture {
    fun createLoginCommand() = LoginCommand("zunza@email.com", "password1!")
}