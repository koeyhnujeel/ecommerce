package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerRegistrationService
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.application.fixture.AddressCommandFixture
import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CustomerRegisterTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var customerRegister: CustomerRegister

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        customerRegister = CustomerRegistrationService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerCustomer() {
        val registerCommand = CustomerRegisterCommand(1L, "홍길동", "01012345678")

        val customer = mockk<Customer>()

        mockkObject(Customer.Companion)

        every { Customer.register(any(), any(), any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        customerRegister.registerCustomer(registerCommand)

        verify(exactly = 1) {
            Customer.register(registerCommand.accountId, registerCommand.name, registerCommand.phone)
            customerRepository.save(customer)
        }
    }

    @Test
    fun customerRegisterCustomerCommandFail() {
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(0L, "홍길동","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍길동","1012345678") }
    }

    @Test
    fun registerAddress() {
        val accountId = 1L;
        val command = AddressCommandFixture.createRegisterAddressCommand(accountId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        val result = customerRegister.registerShippingAddress(command)

        result shouldBe accountId

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.registerShippingAddress(
                command.alias,
                command.roadAddress,
                command.detailAddress,
                command.receiverName,
                command.zipcode,
                command.isDefault
            )
        }
    }

    @Test
    fun registerAddressFailCustomerNotFound() {
        val accountId = 1L;
        val command = AddressCommandFixture.createRegisterAddressCommand(accountId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerRegister.registerShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.registerShippingAddress(
                command.alias,
                command.roadAddress,
                command.detailAddress,
                command.receiverName,
                command.zipcode,
                command.isDefault
            )
        }
    }
}