package com.zunza.api.customer.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.api.customer.config.TestContainersConfig
import com.zunza.customer.api.domain.customer.dto.request.SignupRequestDto
import com.zunza.domain.enums.UserRole
import com.zunza.domain.repository.CustomerRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
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
class CustomerIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val customerRepository: CustomerRepository,
) : FunSpec({

        test("회원가입 성공: Customer 정보를 저장하고, 201을 반환한다.") {
            val request = SignupRequestDto("xxx@example.com", "password1!", "홍길동", "010-1234-5678")

            mockMvc
                .post("/api/customers") {
                    content = objectMapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.result") { "SUCCESS" }
                }

            val saved = customerRepository.findAll().first()
            saved.email shouldBe "xxx@example.com"
            passwordEncoder.matches("password1!", saved.password) shouldBe true
            saved.name shouldBe "홍길동"
            saved.nickname.shouldNotBeNull()
            saved.phone shouldBe "010-1234-5678"
            saved.userRole shouldBe UserRole.ROLE_CUSTOMER
            saved.point shouldBe 0L
        }
    })
