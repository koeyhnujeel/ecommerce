package com.zunza.ecommerce.service

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.domain.enums.UserType
import com.zunza.ecommerce.dto.command.LoginCommand
import com.zunza.ecommerce.dto.result.AuthenticateResult
import com.zunza.ecommerce.dto.command.SignupCommand
import com.zunza.ecommerce.dto.result.RefreshTokenResult
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.repository.RefreshTokenRepository
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import com.zunza.ecommerce.repository.UserRepository
import com.zunza.ecommerce.support.exception.ErrorCode
import com.zunza.ecommerce.util.NicknameGenerator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
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

    fun authenticate(command: LoginCommand): AuthenticateResult {
        val user = userRepository.findByEmailOrNull(command.email)
            ?: throw ErrorCode.INVALID_CREDENTIALS.exception()

        if (!passwordEncoder.matches(command.password, user.password)) {
            throw ErrorCode.INVALID_CREDENTIALS.exception()
        }

        val accessToken = tokenProvider.generateAccessToken(user.id, user.userType)
        val refreshToken = tokenProvider.generateRefreshToken(user.id)
        refreshTokenRepository.save(user.id, refreshToken)

        return AuthenticateResult.of(accessToken, refreshToken)
    }

    fun invalidateToken(token: String, userId: Long) {
        val remainingTime = tokenProvider.getRemainingTime(token)

        if (remainingTime > 0) {
            val jti = tokenProvider.getJti(token)
            tokenBlacklistRepository.add(jti, token, remainingTime)
        }

        refreshTokenRepository.deleteById(userId)
    }

    fun refreshToken(expiredToken: String, refreshToken: String): RefreshTokenResult {
        tokenProvider.validateToken(refreshToken)

        val userId = tokenProvider.getUserId(expiredToken)
        val foundRefreshToken = refreshTokenRepository.findByUserId(userId)
            ?: throw ErrorCode.REFRESH_TOKEN_NOT_FOUND.exception()

        if (refreshToken != foundRefreshToken) {
            throw ErrorCode.INVALID_REFRESH_TOKEN.exception()
        }

        val userType = getUserType(expiredToken)
        val newAccessToken = tokenProvider.generateAccessToken(userId, userType)
        val newRefreshToken = tokenProvider.generateRefreshToken(userId)
        refreshTokenRepository.save(userId, newRefreshToken)

        return RefreshTokenResult.of(newAccessToken, newRefreshToken)
    }

    private fun getRandomNickname(): String =
        generateSequence { NicknameGenerator.generate() }
            .first { !customerRepository.existsByNickname(it) }

    private fun getUserType(token: String): UserType {
        val userRole = tokenProvider.getUserRole(token)
        return UserType.entries.find { it.name == userRole }
            ?: throw ErrorCode.INVALID_USER_ROLE.exception()
    }
}
