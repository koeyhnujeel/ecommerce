package com.zunza.ecommerce.service

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.dto.command.LoginCommand
import com.zunza.ecommerce.dto.result.LoginResult
import com.zunza.ecommerce.dto.command.SignupCommand
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.repository.RefreshTokenRepository
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import com.zunza.ecommerce.repository.UserRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import com.zunza.ecommerce.util.NicknameGenerator
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenBlacklistRepository: TokenBlacklistRepository
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

    fun authenticate(command: LoginCommand): LoginResult {
        val user = userRepository.findByEmailOrNull(command.email)
            ?: throw ErrorCode.INVALID_CREDENTIALS.exception()

        if (!passwordEncoder.matches(command.password, user.password)) {
            throw ErrorCode.INVALID_CREDENTIALS.exception()
        }

        val accessToken = tokenProvider.generateAccessToken(user.id, user.userType)
        val refreshToken = tokenProvider.generateRefreshToken(user.id)
        refreshTokenRepository.save(user.id, refreshToken)

        return LoginResult(accessToken, refreshToken)
    }

    fun invalidateToken(token: String, userId: Long) {
        val remainingTime = tokenProvider.getRemainingTime(token)

        if (remainingTime > 0) {
            val jti = tokenProvider.getJti(token)
            tokenBlacklistRepository.add(jti, token, remainingTime)
        }

        refreshTokenRepository.deleteById(userId)
    }

    private fun getRandomNickname(): String =
        generateSequence { NicknameGenerator.generate() }
            .first { !customerRepository.existsByNickname(it) }
}
