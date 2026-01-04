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

class UpdateShippingAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var updateShippingAddressUseCase: UpdateShippingAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        updateShippingAddressUseCase = CustomerCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun updateAddress() {
        val accountId = 1L;
        val command = AddressCommandFixture.createUpdateAddressCommand(accountId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        updateShippingAddressUseCase.updateShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.updateShippingAddress(
                addressId = command.addressId,
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
    fun updateAddressFailCustomerNotFound() {
        val accountId = 1L;
        val command = AddressCommandFixture.createUpdateAddressCommand(accountId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            updateShippingAddressUseCase.updateShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.updateShippingAddress(
                addressId = command.addressId,
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