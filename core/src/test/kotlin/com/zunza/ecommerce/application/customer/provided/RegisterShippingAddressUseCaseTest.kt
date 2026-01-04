package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerCommandService
import com.zunza.ecommerce.application.fixture.AddressCommandFixture
import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterShippingAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var registerShippingAddressUseCase: RegisterShippingAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        registerShippingAddressUseCase = CustomerCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
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

        val result = registerShippingAddressUseCase.registerShippingAddress(command)

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
            registerShippingAddressUseCase.registerShippingAddress(command)
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