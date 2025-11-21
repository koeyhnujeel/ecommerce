package com.zunza.ecommerce.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.config.TestContainersConfig
import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.dto.SignupRequest
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.repository.CustomerRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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
) : FunSpec({

    beforeSpec {
        val customer = Customer.of(
            "xxx@example.com",
            "password1!",
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
            .post("/api/auth/signup/customer") {
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
})
