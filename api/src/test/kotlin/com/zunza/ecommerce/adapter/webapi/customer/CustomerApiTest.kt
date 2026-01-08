package com.zunza.ecommerce.adapter.webapi.customer

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.RegisterAddressRequest
import com.zunza.ecommerce.adapter.webapi.customer.dto.request.UpdateAddressRequest
import com.zunza.ecommerce.application.account.provided.AccountAuthenticator
import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.account.provided.AccountRegister
import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.customer.provided.CustomerRegister
import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.dto.command.RegisterShippingAddressCommand
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
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class CustomerApiTest(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val accountManager: AccountManager,
    val accountRegister: AccountRegister,
    val customerRegister: CustomerRegister,
    val customerRepository: CustomerRepository,
    val accountAuthenticator: AccountAuthenticator,
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

        accountId = accountRegister.registerCustomerAccount(registerCommand)

        accountManager.activateCustomerAccount(accountId)

        val loginCommand = LoginCommand(registerCommand.email, registerCommand.password)

        val loginResult = accountAuthenticator.login(loginCommand)

        accessToken = loginResult.accessToken
        refreshToken = loginResult.refreshToken
    }

    @Test
    fun registerShippingAddress() {
        val request = RegisterAddressRequest(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )

        mockMvc.post("/api/customers/me/shipping-addresses") {
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

        val customer = customerRepository.findWithShippingAddressesOrThrow(accountId)

        customer.shippingAddresses shouldHaveSize 1
        customer.shippingAddresses[0].alias shouldBe request.alias
        customer.shippingAddresses[0].receiverName shouldBe request.receiverName
        customer.shippingAddresses[0].isDefault shouldBe request.isDefault
        customer.shippingAddresses[0].address.roadAddress shouldBe request.roadAddress
        customer.shippingAddresses[0].address.detailAddress shouldBe request.detailAddress
        customer.shippingAddresses[0].address.zipcode shouldBe request.zipcode
    }

    @Test
    fun updateShippingAddress() {
        val command = createRegisterShippingAddressCommand(accountId)

        customerRegister.registerShippingAddress(command)

        val customer = customerRepository.findWithShippingAddressesOrThrow(accountId)

        val addressId = customer.shippingAddresses[0].id

        val request = UpdateAddressRequest(
            alias = "수정된 집",
            roadAddress = "서울특별시 관악구 관악로 111",
            detailAddress = "",
            receiverName = "이순신",
            zipcode = "13232",
            isDefault = true
        )

        mockMvc.put("/api/customers/me/shipping-addresses/${addressId}") {
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

        customer.shippingAddresses shouldHaveSize 1
        customer.shippingAddresses[0].alias shouldBe request.alias
        customer.shippingAddresses[0].receiverName shouldBe request.receiverName
        customer.shippingAddresses[0].isDefault shouldBe request.isDefault
        customer.shippingAddresses[0].address.roadAddress shouldBe request.roadAddress
        customer.shippingAddresses[0].address.detailAddress shouldBe request.detailAddress
        customer.shippingAddresses[0].address.zipcode shouldBe request.zipcode
    }

    @Test
    fun updateShippingDefaultAddress() {
        val command1 = createRegisterShippingAddressCommand(accountId)

        customerRegister.registerShippingAddress(command1)

        val command2 = createRegisterShippingAddressCommand(
            accountId,
            "회사",
            "서울특별시 관악구 관악로 123",
            "",
            "홍길동",
            "22222",
            false
        )

        customerRegister.registerShippingAddress(command2)

        val customer = customerRepository.findWithShippingAddressesOrThrow(accountId)

        customer.shippingAddresses[0].isDefault shouldBe true
        customer.shippingAddresses[1].isDefault shouldBe false

        val addressId = customer.shippingAddresses[1].id

        mockMvc.patch("/api/customers/me/shipping-addresses/${addressId}") {
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

        customer.shippingAddresses[0].isDefault shouldBe false
        customer.shippingAddresses[1].isDefault shouldBe true
    }

    @Test
    fun deleteShippingAddress() {
        val command = createRegisterShippingAddressCommand(accountId)

        customerRegister.registerShippingAddress(command)

        val customer = customerRepository.findWithShippingAddressesOrThrow(accountId)
        val addressId = customer.shippingAddresses[0].id

        customer.shippingAddresses shouldHaveSize 1

        mockMvc.delete("/api/customers/me/shipping-addresses/${addressId}") {
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

        customer.shippingAddresses shouldHaveSize 0
    }

    private fun createRegisterShippingAddressCommand(
        accountId: Long,
        alias: String = "집",
        roadAddress: String = "서울특별시 관악구 관악로 1",
        detailAddress: String = "",
        receiverName: String = "홍길동",
        zipcode: String = "11111",
        isDefault: Boolean = true
    ) =
        RegisterShippingAddressCommand(
            accountId = accountId,
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = isDefault
        )
}