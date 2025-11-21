package com.zunza.ecommerce.service

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.dto.LoginCommand
import com.zunza.ecommerce.dto.SignupCommand
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.repository.UserRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import com.zunza.ecommerce.util.NicknameGenerator
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun validateEmailAvailable(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw ErrorCode.EMAIL_ALREADY_EXISTS.exception()
        }
    }

    fun validatePhoneAvailable(phone: String) {
        if (customerRepository.existsByPhone(phone)) {
            throw ErrorCode.PHONE_ALREADY_EXISTS.exception()
        }
    }

    fun createCustomer(command: SignupCommand) {
        val encodedPassword = passwordEncoder.encode(command.password)
        val nickname = getRandomNickname()

        val customer = Customer.of(
            command.email,
            encodedPassword,
            command.name,
            nickname,
            command.phone
        )

        customerRepository.save(customer)
    }

    private fun getRandomNickname(): String =
        generateSequence { NicknameGenerator.generate() }
            .first { !customerRepository.existsByNickname(it) }
}
