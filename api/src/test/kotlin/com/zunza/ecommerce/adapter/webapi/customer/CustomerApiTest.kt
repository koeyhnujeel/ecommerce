package com.zunza.ecommerce.adapter.webapi.customer

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.RegisterAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.UpdateAddressRequest
import com.zunza.ecommerce.application.account.provided.ActivateCustomerAccountUseCase
import com.zunza.ecommerce.application.account.provided.LoginUseCase
import com.zunza.ecommerce.application.account.provided.RegisterCustomerAccountUseCase
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.customer.provided.RegisterAddressUseCase
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.RegisterAddressCommand
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class CustomerApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val loginUseCase: LoginUseCase,
    val customerRepository: CustomerRepository,
    val registerAddressUseCase: RegisterAddressUseCase,
    val registerCustomerAccountUseCase: RegisterCustomerAccountUseCase,
    val activateCustomerAccountUseCase: ActivateCustomerAccountUseCase,
) {
    var accountId: Long = 0
    lateinit var accessToken: String
    lateinit var refreshToken: String

    @BeforeEach
    fun setUp() {
        val registerCommand = AccountRegisterCommand(
            email = "zunza@email.com",
            password = "password1!",
            name = "홍길동",
            phone = "01012345678",
        )

        accountId = registerCustomerAccountUseCase.registerCustomerAccount(registerCommand)

        activateCustomerAccountUseCase.activateCustomerAccount(accountId)

        val loginCommand = LoginCommand(registerCommand.email, registerCommand.password)

        val loginResult = loginUseCase.login(loginCommand)

        accessToken = loginResult.accessToken
        refreshToken = loginResult.refreshToken
    }

    @Test
    fun registerAddress() {
        val request = RegisterAddressRequest(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )

        mockMvc.post("/api/customers/me/addresses") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
            )
        }.andExpect {
            status { isCreated() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.customerId") { exists() }
            jsonPath("$.timestamp") { exists() }
        }

        val customer = customerRepository.findWithAddressesOrThrow(accountId)

        customer.addresses shouldHaveSize 1
        customer.addresses[0].alias shouldBe request.alias
        customer.addresses[0].roadAddress shouldBe request.roadAddress
        customer.addresses[0].detailAddress shouldBe request.detailAddress
        customer.addresses[0].receiverName shouldBe request.receiverName
        customer.addresses[0].zipcode shouldBe request.zipcode
        customer.addresses[0].isDefault shouldBe request.isDefault
    }

    @Test
    fun updateAddress() {
        val command = createRegisterAddressCommand(accountId)

        registerAddressUseCase.registerAddress(command)

        val customer = customerRepository.findWithAddressesOrThrow(accountId)

        val addressId = customer.addresses[0].id

        val request = UpdateAddressRequest(
            alias = "수정된 집",
            roadAddress = "서울특별시 관악구 관악로 111",
            detailAddress = "",
            receiverName = "이순신",
            zipcode = "13232",
            isDefault = true
        )

        mockMvc.put("/api/customers/me/addresses/${addressId}") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
            )
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        customer.addresses shouldHaveSize 1
        customer.addresses[0].alias shouldBe request.alias
        customer.addresses[0].roadAddress shouldBe request.roadAddress
        customer.addresses[0].detailAddress shouldBe request.detailAddress
        customer.addresses[0].receiverName shouldBe request.receiverName
        customer.addresses[0].zipcode shouldBe request.zipcode
        customer.addresses[0].isDefault shouldBe request.isDefault
    }

    @Test
    fun updateDefaultAddress() {
        val command1 = createRegisterAddressCommand(accountId)

        registerAddressUseCase.registerAddress(command1)

        val command2 = createRegisterAddressCommand(
            accountId,
            "회사",
            "서울특별시 관악구 관악로 123",
            "",
            "홍길동",
            "22222",
            false
        )

        registerAddressUseCase.registerAddress(command2)

        val customer = customerRepository.findWithAddressesOrThrow(accountId)

        customer.addresses[0].isDefault shouldBe true
        customer.addresses[1].isDefault shouldBe false

        val addressId = customer.addresses[1].id

        mockMvc.patch("/api/customers/me/addresses/${addressId}") {
            contentType = MediaType.APPLICATION_JSON
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
            )
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        customer.addresses[0].isDefault shouldBe false
        customer.addresses[1].isDefault shouldBe true
    }

    @Test
    fun deleteAddress() {
        val command = createRegisterAddressCommand(accountId)

        registerAddressUseCase.registerAddress(command)

        val customer = customerRepository.findWithAddressesOrThrow(accountId)
        val addressId = customer.addresses[0].id

        customer.addresses shouldHaveSize 1

        mockMvc.delete("/api/customers/me/addresses/${addressId}") {
            contentType = MediaType.APPLICATION_JSON
            cookie(
                Cookie("accessToken", accessToken),
                Cookie("refreshToken", refreshToken)
            )
        }.andExpect {
            status { isNoContent() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.timestamp") { exists() }
        }

        customer.addresses shouldHaveSize 0
    }

    private fun createRegisterAddressCommand(
        accountId: Long,
        alias: String = "집",
        roadAddress: String = "서울특별시 관악구 관악로 1",
        detailAddress: String = "",
        receiverName: String = "홍길동",
        zipcode: String = "11111",
        isDefault: Boolean = true
    ) =
        RegisterAddressCommand(
            accountId = accountId,
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = isDefault
        )
}