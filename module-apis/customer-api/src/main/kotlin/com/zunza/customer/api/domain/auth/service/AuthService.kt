package com.zunza.customer.api.domain.auth.service

import com.zunza.auth.jwt.JwtProvider
import com.zunza.common.support.exception.CustomTokenException
import com.zunza.common.support.exception.ErrorCode
import com.zunza.customer.api.domain.auth.dto.LoginResultDto
import com.zunza.customer.api.domain.auth.dto.RefreshResultDto
import com.zunza.customer.api.domain.auth.dto.request.LoginRequestDto
import com.zunza.domain.enums.UserRole
import com.zunza.domain.repository.CustomerRepository
import com.zunza.infra.redis.RefreshTokenRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun login(request: LoginRequestDto): LoginResultDto {
        val customer =
            customerRepository.findByEmail(request.email)
                ?: throw ErrorCode.UNAUTHORIZED.exception("이메일 또는 비밀번호를 확인해 주세요.")

        if (!passwordEncoder.matches(request.password, customer.password)) {
            throw ErrorCode.UNAUTHORIZED.exception("이메일 또는 비밀번호를 확인해 주세요.")
        }

        val accessToken = jwtProvider.generateAccessToken(customer.id, customer.userRole)
        val refreshToken = jwtProvider.generateRefreshToken(customer.id)

        refreshTokenRepository.save(customer.id, refreshToken)

        return LoginResultDto.of(customer.nickname, accessToken, refreshToken)
    }

    fun logout(customerId: Long) {
        refreshTokenRepository.delete(customerId)
    }

    fun tokenRefresh(
        bearerToken: String,
        refreshToken: String,
    ): RefreshResultDto {
        try {
            jwtProvider.validateToken(refreshToken)

            val accessToken = bearerToken.substring("Bearer ".length)
            val claims = jwtProvider.parseClaims(accessToken)
            val customerId = claims.subject.toLong()

            val foundRefreshToken =
                refreshTokenRepository.findByUserId(customerId)
                    ?: throw ErrorCode.NOT_FOUND.exception("Refresh 토큰을 찾을 수 없습니다.")

            if (foundRefreshToken != refreshToken) {
                throw ErrorCode.BAD_REQUEST.exception("유효하지 않은 토큰입니다.")
            }

            val role = claims["role"] as String
            val userRole = UserRole.entries.find { it.name == role }

            val newAccessToken = jwtProvider.generateAccessToken(customerId, userRole!!)
            val newRefreshToken = jwtProvider.generateRefreshToken(customerId)
            refreshTokenRepository.save(customerId, newRefreshToken)

            return RefreshResultDto.of(newAccessToken, newRefreshToken)
        } catch (e: CustomTokenException) {
            throw e
        }
    }
}
