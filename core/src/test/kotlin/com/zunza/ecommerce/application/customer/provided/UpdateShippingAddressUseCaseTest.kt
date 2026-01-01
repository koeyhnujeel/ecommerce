package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
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
    lateinit var updateAddressUseCase: UpdateAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        updateAddressUseCase = CustomerCommandService(customerRepository)
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

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        updateAddressUseCase.updateAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
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

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            updateAddressUseCase.updateAddress(command)
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