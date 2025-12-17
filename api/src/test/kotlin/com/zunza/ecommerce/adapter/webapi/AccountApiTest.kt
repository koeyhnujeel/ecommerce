package com.zunza.ecommerce.adapter.webapi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.persistence.account.AccountJpaRepository
import com.zunza.ecommerce.adapter.persistence.customer.CustomerJpaRepository
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.service.dto.request.AccountActivateRequest
import com.zunza.ecommerce.application.account.service.dto.response.AccountRegisterResponse
import com.zunza.ecommerce.application.fixture.AccountRequestFixture
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import com.zunza.ecommerce.domain.account.AccountStatus
import com.zunza.ecommerce.domain.account.UserRole
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class AccountApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val accountRegister: AccountRegister,
    val accountJpaRepository: AccountJpaRepository,
    val customerJpaRepository: CustomerJpaRepository
) {
    @Test
    fun register() {
        val registerRequest = AccountRequestFixture.createAccountRegisterRequest()

        val result = mockMvc.post("/api/accounts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.accountId") { exists() }
            jsonPath("$.data.email") { value(registerRequest.email) }
            jsonPath("$.data.registeredAt") { exists() }
            jsonPath("$.timestamp") { exists() }
        }.andReturn()

        val response: ApiResponse<AccountRegisterResponse> = objectMapper.readValue(result.response.contentAsString)

        val account = accountJpaRepository.findByIdOrNull(response.data!!.accountId)
            ?: throw NoSuchElementException()

        account.email.address shouldBe registerRequest.email
        account.passwordHash shouldNotBe null
        account.status shouldBe AccountStatus.PENDING
        account.role shouldBe UserRole.ROLE_CUSTOMER
        account.registeredAt shouldNotBe null
        account.activatedAt shouldBe null
        account.deactivatedAt shouldBe null
        account.lastLoginAt shouldBe null

        val customer = customerJpaRepository.findByAccountId(account.id)
            ?: throw NoSuchElementException()

        customer.name shouldBe registerRequest.name
        customer.phone shouldBe registerRequest.phone
    }

    @Test
    fun activate() {
        val registerResponse = accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest())

        val activateRequest = AccountActivateRequest(registerResponse.accountId)

        mockMvc.post("/api/accounts/activation") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(activateRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.accountId") { value(registerResponse.accountId) }
            jsonPath("$.data.email") { value(registerResponse.email) }
            jsonPath("$.data.activatedAt") { exists() }
            jsonPath("$.timestamp") { exists() }
        }.andReturn()

        val account = accountJpaRepository.findByIdOrNull(activateRequest.accountId)
            ?: throw NoSuchElementException()

        account.status shouldBe AccountStatus.ACTIVE
        account.activatedAt shouldNotBe null
    }
}