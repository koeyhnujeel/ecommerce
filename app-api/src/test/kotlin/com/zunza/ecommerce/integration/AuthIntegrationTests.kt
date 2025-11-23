package com.zunza.ecommerce.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.config.TestContainersConfig
import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.domain.enums.UserType
import com.zunza.ecommerce.dto.request.LoginRequest
import com.zunza.ecommerce.dto.request.SignupRequest
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.repository.RefreshTokenRepository
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.servlet.http.Cookie
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@SpringBootTest
class AuthIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository,
    @Autowired private val tokenProvider: TokenProvider,
    @Autowired private val tokenBlacklistRepository: TokenBlacklistRepository,
) : FunSpec({

    beforeSpec {
        val customer = Customer.of(
            "xxx@example.com",
            passwordEncoder.encode("password1!"),
            "홍길동",
            "행복한 호랑이 1",
            "010-1234-5678"
        )

        customerRepository.save(customer)
    }

    test("사용 가능한 이메일이면 200을 응답한다.") {
        mockMvc
            .get("/api/auth/email/validation") {
                queryParam("email", "new@example.com")
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                jsonPath("$.result") { value("SUCCESS") }
            }
    }

    test("이미 사용 중인 이메일이면 409을 응답한다.") {
        mockMvc
            .get("/api/auth/email/validation") {
                queryParam("email", "xxx@example.com")
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isConflict() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("이미 사용 중인 이메일입니다.") }
            }
    }

    test("사용 가능한 핸드폰 번호면 200을 응답한다.") {
        mockMvc
            .get("/api/auth/phone/validation") {
                queryParam("phone", "010-2222-3333")
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                jsonPath("$.result") { value("SUCCESS") }
            }
    }

    test("이미 사용 중인 핸드폰 번호면 409을 응답한다.") {
        mockMvc
            .get("/api/auth/phone/validation") {
                queryParam("phone", "010-1234-5678")
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isConflict() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("이미 사용 중인 번호입니다.") }
            }
    }

    test("회원가입 성공 시 고객 정보를 저장하고 201을 응답한다.") {
        val request = SignupRequest("new@example.com", "password1!", "홍길동", "010-1111-5678")

        mockMvc
            .post("/api/auth/signup") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isCreated() }
                jsonPath("$.result") { value("SUCCESS") }
            }

        val customer = customerRepository.findByIdOrThrow(2L)
        customer.email shouldBe request.email
        passwordEncoder.matches(request.password, customer.password) shouldBe true
        customer.name shouldBe request.name
        customer.nickname shouldNotBe null
        customer.phone shouldBe request.phone
        customer.point shouldBe 0L
    }

    test("로그인 성공 시 리프레시 토큰을 저장하고 200, 액세스(응답 본문), 리프레시(쿠키) 토큰을 응답한다.") {
        val request = LoginRequest("xxx@example.com", "password1!")

        mockMvc
            .post("/api/auth/login") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }
                jsonPath("$.result") { value("SUCCESS") }
                jsonPath("$.data.accessToken") { exists() }
                cookie { exists("refreshToken") }
            }

        val refreshToken = refreshTokenRepository.findByUserId(1L)
        refreshToken shouldNotBe null
    }

    test("로그인 실패 시(유효하지 않은 이메일) 401을 응답한다.") {
        val request = LoginRequest("qqq@example.com", "password1!")

        mockMvc
            .post("/api/auth/login") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
            }
    }

    test("로그인 실패 시(유효하지 않은 비밀번호) 401을 응답한다.") {
        val request = LoginRequest("xxx@example.com", "invalid_password")

        mockMvc
            .post("/api/auth/login") {
                content = objectMapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isUnauthorized() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
            }
    }

    test("로그아웃 성공 시 액세스 토큰을 블랙리스트 등록하고 리프레시 토큰은 저장소에서 삭제한다.") {
        val accessToken = tokenProvider.generateAccessToken(3L, UserType.CUSTOMER)
        val refreshToken = tokenProvider.generateRefreshToken(3L)
        refreshTokenRepository.save(3L, refreshToken)

        mockMvc
            .post("/api/auth/logout") {
                header("Authorization", "Bearer $accessToken")
            }.andExpect {
                status { isOk() }
                jsonPath("$.result") { value("SUCCESS") }
                cookie {
                    maxAge("refreshToken", 0)
                    value("refreshToken", "")
                }
            }

        val jti = tokenProvider.getJti(accessToken)
        tokenBlacklistRepository.existsByJti(jti) shouldBe true
        refreshTokenRepository.findByUserId(3L) shouldBe null
    }

    test("유효한 토큰으로 재발급 요청 시 새로운 액세스 토큰과 리프레시 토큰을 응답한다") {
        val userId = 4L
        val expiredToken = tokenProvider.generateAccessToken(userId, UserType.CUSTOMER)
        val refreshToken = tokenProvider.generateRefreshToken(userId)
        refreshTokenRepository.save(userId, refreshToken)

        mockMvc
            .post("/api/auth/refresh") {
                header("Authorization", "Bearer $expiredToken")
                cookie(Cookie("refreshToken", refreshToken))
            }.andExpect {
                status { isOk() }
                jsonPath("$.result") { value("SUCCESS") }
                jsonPath("$.data.accessToken") { exists() }
                cookie {
                    exists("refreshToken")
                    maxAge("refreshToken", 7 * 24 * 60 * 60)
                }
            }

        val newRefreshToken = refreshTokenRepository.findByUserId(userId)
        newRefreshToken shouldNotBe null
        newRefreshToken shouldNotBe refreshToken
    }
})
