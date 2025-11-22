package com.zunza.ecommerce.unit

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.domain.User
import com.zunza.ecommerce.domain.enums.UserType
import com.zunza.ecommerce.dto.command.LoginCommand
import com.zunza.ecommerce.dto.command.SignupCommand
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.repository.RefreshTokenRepository
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import com.zunza.ecommerce.repository.UserRepository
import com.zunza.ecommerce.service.AuthService
import com.zunza.ecommerce.support.exception.BusinessException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.UUID

class AuthServiceUnitTests : FunSpec ({
    val customerRepository = mockk<CustomerRepository>()
    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val tokenProvider = mockk<TokenProvider>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()
    val tokenBlacklistRepository = mockk<TokenBlacklistRepository>()

    val authService = AuthService(
        customerRepository,
        userRepository,
        passwordEncoder,
        tokenProvider,
        refreshTokenRepository,
        tokenBlacklistRepository,
    )

    afterTest {
        clearAllMocks()
    }

    val user = User(
        id = 1L,
        email = "xxx@example.com",
        password = "password1!",
        userType = UserType.CUSTOMER,
        LocalDateTime.now(),
        LocalDateTime.now()
    )

    test("사용 가능한 이메일이면 예외를 던지지 않는다.") {
        val email = "new@email.com"
        every { userRepository.existsByEmail(email) } returns false

        shouldNotThrow<BusinessException> {
            authService.validateEmailAvailable(email)
        }
    }

    test("이메일이 이미 존재하면 예외를 던진다.") {
        val email = "duplicate@email.com"
        every { userRepository.existsByEmail(email) } returns true

        val exception = shouldThrow<BusinessException> {
            authService.validateEmailAvailable(email)
        }

        exception.errorCode.status shouldBe 409
        exception.message shouldBe "이미 사용 중인 이메일입니다."
    }

    test("사용 가능한 핸드폰 번호면 예외를 던지지 않는다.") {
        val phone = "010-1234-5678"
        every { customerRepository.existsByPhone(phone) } returns false

        shouldNotThrow<BusinessException> {
            authService.validatePhoneAvailable(phone)
        }
    }

    test("핸드폰 번호가 이미 존재하면 예외를 던진다.") {
        val phone = "010-1234-5678"
        every { customerRepository.existsByPhone(phone) } returns true

        val exception = shouldThrow<BusinessException> {
            authService.validatePhoneAvailable(phone)
        }

        exception.errorCode.status shouldBe 409
        exception.message shouldBe "이미 사용 중인 번호입니다."
    }

    test("회원가입 요청 시 고객이 생성되어 저장된다.") {
        val command = SignupCommand("new@example.com", "password1!", "홍길동", "010-1234-5678")
        val encodedPassword = "encoded_password"

        every { passwordEncoder.encode(command.password) } returns encodedPassword
        every { customerRepository.existsByNickname(any<String>()) } returns false
        every { customerRepository.save(any<Customer>()) } returns mockk<Customer>()

        authService.createCustomer(command)

        verify(exactly = 1) { passwordEncoder.encode(command.password) }
        verify(exactly = 1) { customerRepository.existsByNickname(any<String>()) }
        verify(exactly = 1) { customerRepository.save(any<Customer>()) }
    }

    test("로그인 시 액세스 토큰과 리프레시 토큰을 반환한다.") {
        val command = LoginCommand("xxx@example.com", "password1!")
        val accessToken = "_accessToken_"
        val refreshToken = "_refreshToken_"

        every { userRepository.findByEmailOrNull(command.email) } returns user
        every { passwordEncoder.matches(command.password, any<String>()) } returns true
        every { tokenProvider.generateAccessToken(user.id, user.userType) } returns accessToken
        every { tokenProvider.generateRefreshToken(user.id) } returns refreshToken
        every { refreshTokenRepository.save(user.id, refreshToken) } returns Unit

        val loginResult = authService.authenticate(command)

        loginResult.accessToken shouldBe accessToken
        loginResult.refreshToken shouldBe refreshToken
        verify(exactly = 1) { userRepository.findByEmailOrNull(command.email) }
        verify(exactly = 1) { passwordEncoder.matches(command.password, any<String>()) }
        verify(exactly = 1) { tokenProvider.generateAccessToken(user.id, user.userType) }
        verify(exactly = 1) { tokenProvider.generateRefreshToken(user.id) }
        verify(exactly = 1) { refreshTokenRepository.save(user.id, refreshToken) }
    }

    test("유효하지 않는 이메일로 로그인 시 예외를 던진다.") {
        val command = LoginCommand("xxx@example.com", "password1!")

        every { userRepository.findByEmailOrNull(command.email) } returns null

        val exception = shouldThrow<BusinessException> {
            authService.authenticate(command)
        }

        exception.errorCode.status shouldBe 401
        exception.message shouldBe "이메일 또는 비밀번호를 확인해 주세요."
        verify(exactly = 1) { userRepository.findByEmailOrNull(command.email) }
        verify(exactly = 0) { passwordEncoder.matches(command.password, any<String>()) }
        verify(exactly = 0) { tokenProvider.generateAccessToken(any<Long>(), any<UserType>()) }
        verify(exactly = 0) { tokenProvider.generateRefreshToken(any<Long>()) }
        verify(exactly = 0) { refreshTokenRepository.save(any<Long>(), any<String>()) }
    }

    test("유효하지 않는 비밀번호로 로그인 시 예외를 던진다.") {
        val command = LoginCommand("xxx@example.com", "password1!")

        every { userRepository.findByEmailOrNull(command.email) } returns user
        every { passwordEncoder.matches(command.password, any<String>()) } returns false

        val exception = shouldThrow<BusinessException> {
            authService.authenticate(command)
        }

        exception.errorCode.status shouldBe 401
        exception.message shouldBe "이메일 또는 비밀번호를 확인해 주세요."
        verify(exactly = 1) { userRepository.findByEmailOrNull(command.email) }
        verify(exactly = 1) { passwordEncoder.matches(command.password, any<String>()) }
        verify(exactly = 0) { tokenProvider.generateAccessToken(any<Long>(), any<UserType>()) }
        verify(exactly = 0) { tokenProvider.generateRefreshToken(any<Long>()) }
        verify(exactly = 0) { refreshTokenRepository.save(any<Long>(), any<String>()) }
    }

    test("로그아웃 시 액세스 토큰을 블랙리스트에 등록하고 리프레시 토큰은 저장소에서 삭제한다.") {
        val token = "access_token"
        val userId = 1L
        val remainingTime = 3231231243L
        val jti = UUID.randomUUID().toString()

        every { tokenProvider.getRemainingTime(token) } returns remainingTime
        every { tokenProvider.getJti(token) } returns jti
        every { tokenBlacklistRepository.add(jti, token, remainingTime) } returns Unit
        every { refreshTokenRepository.deleteById(userId) } returns true

        authService.invalidateToken(token, userId)

        verify(exactly =  1) { tokenProvider.getRemainingTime(token) }
        verify(exactly =  1) { tokenProvider.getJti(token) }
        verify(exactly =  1) { tokenBlacklistRepository.add(jti, token, remainingTime) }
        verify(exactly =  1) { refreshTokenRepository.deleteById(userId) }
    }
})
