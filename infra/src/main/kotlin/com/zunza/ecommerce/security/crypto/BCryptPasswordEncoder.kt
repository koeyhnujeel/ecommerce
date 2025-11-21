package com.zunza.ecommerce.security.crypto

import at.favre.lib.crypto.bcrypt.BCrypt
import com.zunza.ecommerce.port.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoder(
) : PasswordEncoder {
    private val bcrypt = BCrypt.withDefaults()
    private val verifier = BCrypt.verifyer()

    override fun encode(rawPassword: String): String {
        return bcrypt.hashToString(12, rawPassword.toCharArray())
    }

    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return verifier.verify(rawPassword.toCharArray(), encodedPassword).verified
    }
}
