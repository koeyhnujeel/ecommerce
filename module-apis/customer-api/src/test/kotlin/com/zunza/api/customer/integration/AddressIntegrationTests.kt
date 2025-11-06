package com.zunza.api.customer.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.api.customer.config.TestContainersConfig
import com.zunza.apis.auth.jwt.JwtProvider
import com.zunza.apis.support.exception.ErrorCode
import com.zunza.customer.api.domain.customer.dto.request.AddressRegisterRequestDto
import com.zunza.domain.entity.Customer
import com.zunza.domain.enums.UserRole
import com.zunza.domain.repository.CustomerAddressRepository
import com.zunza.domain.repository.CustomerRepository
import io.kotest.core.spec.style.FunSpec
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
class AddressIntegrationTests(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jwtProvider: JwtProvider,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val customerRepository: CustomerRepository,
    @Autowired private val customerAddressRepository: CustomerAddressRepository
) : FunSpec({

    lateinit var customer: Customer
    lateinit var accessToken: String
    val request = AddressRegisterRequestDto(
        "집",
        "03000",
        "대전광역시 중구 무슨동 111-1",
        "1동 101호",
        true
    )

    beforeSpec {
        customer = customerRepository.save(
            Customer.of(
            "aaa@example.com",
            passwordEncoder.encode("password1!"),
            "심청이",
            "포근한 호랑이 4343",
            "010-1234-1234"
            )
        )

        accessToken = jwtProvider.generateAccessToken(customer.id, customer.userRole)
    }

    test("배송지 등록 성공: CustomerAddress 정보를 저장하고, 201을 반환한다.") {
        mockMvc.post("/api/customers/me/addresses") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }.andExpect {
            status { isCreated() }
            jsonPath("$.result") { "SUCCESS" }
        }

        val address = customerAddressRepository.findByCustomerId(customer.id)
            ?: throw ErrorCode.NOT_FOUND.exception("등록된 배송지가 없습니다.")

        address.alias shouldBe request.alias
        address.zipCode shouldBe request.zipCode
        address.address shouldBe request.address
        address.addressDetail shouldBe request.addressDetail
        address.isDefault shouldBe request.isDefault
    }

    test("배송지 등록 실패: 유효하지 않은 사용자가 배송지 등록을 시도하면 404 예외를 응답한다.") {
        val token = jwtProvider.generateAccessToken(2L, UserRole.ROLE_CUSTOMER)

        mockMvc.post("/api/customers/me/addresses") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.result") { "ERROR" }
            jsonPath("$.error.message") { "사용자를 찾을 수 없습니다." }
        }
    }
})
