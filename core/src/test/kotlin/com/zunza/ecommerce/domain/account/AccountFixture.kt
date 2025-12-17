package com.zunza.ecommerce.domain.account

object AccountFixture {
    fun createPasswordEncoder(): PasswordEncoder =
        object : PasswordEncoder {
            override fun encode(password: String) = password.uppercase()
            override fun matches(password: String, passwordHash: String) = encode(password) == passwordHash
        }

    fun createRegisteredAccount() =
        Account.register("zunza@email.com", "password1!", createPasswordEncoder())
}