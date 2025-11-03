package com.zunza.api.customer.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.api.customer.config.TestContainersConfig
import com.zunza.apis.auth.jwt.JwtProvider
import com.zunza.customer.api.domain.auth.dto.request.LoginRequestDto
import com.zunza.domain.entity.Customer
import com.zunza.domain.repository.CustomerRepository
import com.zunza.infra.redis.RefreshTokenRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@SpringBootTest
class AuthIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jwtProvider: JwtProvider,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository
) : FunSpec({

    lateinit var customer: Customer

    beforeSpec {
        customer = Customer.of(
            "xxx@example.com",
            passwordEncoder.encode("password1!"),
            "홍길동",
            "포근한 호랑이 2",
            "010-1234-5678"
        )
        customerRepository.save(customer)
    }

    test("로그인 성공: 닉네임과 accessToken을 응답 본문에, refreshToken은 쿠키에 담고 200을 응답한다.") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.nickname") { value("포근한 호랑이 2") }
            jsonPath("$.data.accessToken") { exists() }
            cookie { exists("refreshToken") }
        }

        refreshTokenRepository.findByUserId(customer.id) shouldNotBe null
    }

    test("로그인 실패: 가입되지 않은 이메일로 로그인을 시도하면 401을 응답한다.") {
        val loginRequestDto = LoginRequestDto("yyy@example.com", "password1!")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
            jsonPath("$.data.accessToken") { doesNotExist() }
            cookie { doesNotExist("refreshToken") }
        }
    }

    test("로그인 실패: 잘못된 비밀번호로 로그인을 시도하면 401을 응답한다.") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1111")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isUnauthorized() }
            jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
            jsonPath("$.data.accessToken") { doesNotExist() }
            cookie { doesNotExist("refreshToken") }
        }
    }

    test("로그아웃 성공: refreshToken을 저장소에서 삭제하고 200을 응답한다.") {
        val accessToken = jwtProvider.generateAccessToken(customer.id, customer.userRole)
        val refreshToken = jwtProvider.generateRefreshToken(customer.id)
        refreshTokenRepository.save(customer.id, refreshToken)

        mockMvc.post("/api/auth/logout") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isOk() }
            jsonPath("$.result") { value("SUCCESS") }
            cookie {
                maxAge("refreshToken", 0)
                value("refreshToken", "")
            }
        }

        refreshTokenRepository.findByUserId(customer.id) shouldBe null
    }
})
