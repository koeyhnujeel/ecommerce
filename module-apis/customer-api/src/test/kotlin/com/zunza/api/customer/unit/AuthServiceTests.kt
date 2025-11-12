package com.zunza.api.customer.unit

import com.zunza.auth.jwt.JwtProvider
import com.zunza.common.support.exception.BusinessException
import com.zunza.common.support.exception.CustomTokenException
import com.zunza.customer.api.domain.auth.dto.request.LoginRequestDto
import com.zunza.customer.api.domain.auth.service.AuthService
import com.zunza.domain.entity.Customer
import com.zunza.domain.enums.UserRole
import com.zunza.domain.repository.CustomerRepository
import com.zunza.infra.redis.RefreshTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.impl.DefaultClaims
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.collections.mapOf

class AuthServiceTests :
    FunSpec({
        val customerRepository = mockk<CustomerRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val jwtProvider = mockk<JwtProvider>()
        val refreshTokenRepository = mockk<RefreshTokenRepository>()
        val authService =
            AuthService(
                customerRepository,
                passwordEncoder,
                jwtProvider,
                refreshTokenRepository,
            )

        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")
        val customer =
            Customer(
                1,
                "xxx@example.com",
                "password1!",
                "홍길동",
                "행복한 호랑이 1",
                "010-1234-5678",
                0L,
                UserRole.ROLE_CUSTOMER,
            )

        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val bearerToken = "Bearer $accessToken"

        afterTest {
            clearAllMocks()
        }

        test("로그인 성공 시 닉네임과 accessToken과 refreshToken을 반환한다.") {
            every { customerRepository.findByEmail(any()) } returns customer
            every { passwordEncoder.matches(any(), any()) } returns true
            every { jwtProvider.generateAccessToken(any(), any()) } returns accessToken
            every { jwtProvider.generateRefreshToken(any()) } returns refreshToken
            every { refreshTokenRepository.save(any(), any()) } returns Unit

            val result = authService.login(loginRequestDto)

            result.nickname shouldBe customer.nickname
            result.accessToken shouldBe accessToken
            result.refreshToken shouldBe refreshToken

            verify(exactly = 1) { customerRepository.findByEmail(loginRequestDto.email) }
            verify(exactly = 1) { passwordEncoder.matches(loginRequestDto.password, customer.password) }
            verify(exactly = 1) { jwtProvider.generateAccessToken(customer.id, customer.userRole) }
            verify(exactly = 1) { jwtProvider.generateRefreshToken(customer.id) }
            verify(exactly = 1) { refreshTokenRepository.save(customer.id, refreshToken) }
        }

        test("가입되지 않은 이메일로 로그인 시 UNAUTHORIZED 예외가 발생한다.") {
            every { customerRepository.findByEmail(loginRequestDto.email) } returns null

            val exception =
                shouldThrow<BusinessException> {
                    authService.login(loginRequestDto)
                }

            exception.message shouldBe "이메일 또는 비밀번호를 확인해 주세요."
            exception.errorCode.httpStatus shouldBe HttpStatus.UNAUTHORIZED

            verify(exactly = 1) { customerRepository.findByEmail(loginRequestDto.email) }
            verify(exactly = 0) { passwordEncoder.matches(loginRequestDto.password, customer.password) }
        }

        test("잘못된 비밀번호로 로그인 시 UNAUTHORIZED 예외가 발생한다.") {
            every { customerRepository.findByEmail(loginRequestDto.email) } returns customer
            every { passwordEncoder.matches(any(), any()) } returns false

            val exception =
                shouldThrow<BusinessException> {
                    authService.login(loginRequestDto)
                }

            exception.message shouldBe "이메일 또는 비밀번호를 확인해 주세요."
            exception.errorCode.httpStatus shouldBe HttpStatus.UNAUTHORIZED

            verify(exactly = 1) { customerRepository.findByEmail(loginRequestDto.email) }
            verify(exactly = 1) { passwordEncoder.matches(loginRequestDto.password, customer.password) }
            verify(exactly = 0) { jwtProvider.generateAccessToken(customer.id, customer.userRole) }
        }

        test("로그아웃 시 refreshToken이 저장소에서 삭제된다.") {
            every { refreshTokenRepository.delete(any()) } returns true

            authService.logout(customer.id)

            verify(exactly = 1) { refreshTokenRepository.delete(customer.id) }
        }

        test("유효한 refreshToken 으로 갱신을 요청하면, 새로운 accessToken과 refreshToken을 반환한다.") {
            val foundRefreshToken = "refreshToken"
            val newAccessToken = "newaccesstoken"
            val newRefreshToken = "newrefreshtoken"
            val claims = DefaultClaims(mapOf("role" to "ROLE_CUSTOMER", Claims.SUBJECT to customer.id.toString()))

            every { jwtProvider.validateToken(any()) } returns true
            every { jwtProvider.parseClaims(any()) } returns claims
            every { refreshTokenRepository.findByUserId(any()) } returns foundRefreshToken
            every { jwtProvider.generateAccessToken(any(), any()) } returns newAccessToken
            every { jwtProvider.generateRefreshToken(any()) } returns newRefreshToken
            every { refreshTokenRepository.save(any(), any()) } returns Unit

            val result = authService.tokenRefresh(bearerToken, refreshToken)

            result.newAccessToken shouldBe newAccessToken
            result.newRefreshToken shouldBe newRefreshToken

            verify(exactly = 1) { jwtProvider.validateToken(refreshToken) }
            verify(exactly = 1) { jwtProvider.parseClaims(accessToken) }
            verify(exactly = 1) { refreshTokenRepository.findByUserId(customer.id) }
            verify(exactly = 1) { jwtProvider.generateAccessToken(customer.id, customer.userRole) }
            verify(exactly = 1) { jwtProvider.generateRefreshToken(customer.id) }
            verify(exactly = 1) { refreshTokenRepository.save(customer.id, newRefreshToken) }
        }

        test("유효하지 않은 refreshToken 으로 요청하면 CustomTokenException이 발생한다.") {
            every { jwtProvider.validateToken(any()) } throws CustomTokenException("토큰이 만료되었습니다.")

            val exception =
                shouldThrow<CustomTokenException> {
                    authService.tokenRefresh(bearerToken, refreshToken)
                }

            exception.message shouldBe "토큰이 만료되었습니다."

            verify(exactly = 1) { jwtProvider.validateToken(refreshToken) }
            verify(exactly = 0) { jwtProvider.parseClaims(any()) }
        }

        test("해당 사용자에 refreshToken이 저장소에 없으면 예외를 발생한다.") {
            val claims = DefaultClaims(mapOf("role" to "ROLE_CUSTOMER", Claims.SUBJECT to customer.id.toString()))

            every { jwtProvider.validateToken(any()) } returns true
            every { jwtProvider.parseClaims(any()) } returns claims
            every { refreshTokenRepository.findByUserId(any()) } returns null

            val exception =
                shouldThrow<BusinessException> {
                    authService.tokenRefresh(bearerToken, refreshToken)
                }

            exception.message shouldBe "Refresh 토큰을 찾을 수 없습니다."

            verify(exactly = 1) { jwtProvider.validateToken(refreshToken) }
            verify(exactly = 1) { jwtProvider.parseClaims(accessToken) }
            verify(exactly = 1) { refreshTokenRepository.findByUserId(customer.id) }
            verify(exactly = 0) { jwtProvider.generateAccessToken(customer.id, customer.userRole) }
        }

        test("저장소와 다른 refreshToken 으로 요청하면 예외를 발생한다.") {
            val foundRefreshToken = "differentRefreshToken"
            val claims = DefaultClaims(mapOf("role" to "ROLE_CUSTOMER", Claims.SUBJECT to customer.id.toString()))

            every { jwtProvider.validateToken(any()) } returns true
            every { jwtProvider.parseClaims(any()) } returns claims
            every { refreshTokenRepository.findByUserId(any()) } returns foundRefreshToken

            val exception =
                shouldThrow<BusinessException> {
                    authService.tokenRefresh(bearerToken, refreshToken)
                }

            exception.message shouldBe "유효하지 않은 토큰입니다."

            verify(exactly = 1) { jwtProvider.validateToken(refreshToken) }
            verify(exactly = 1) { jwtProvider.parseClaims(accessToken) }
            verify(exactly = 1) { refreshTokenRepository.findByUserId(customer.id) }
            verify(exactly = 0) { jwtProvider.generateAccessToken(customer.id, customer.userRole) }
        }
    })
