package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerManagementService
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand
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

class CustomerManagerTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var customerManager: CustomerManager

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        customerManager = CustomerManagementService(customerRepository)
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

        customerManager.updateShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
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
            customerManager.updateShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
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
    fun deleteAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer

        customerManager.deleteShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customer.deleteShippingAddress(command.addressId)
        }
    }

    @Test
    fun deleteAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerManager.deleteShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customer.deleteShippingAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer

        customerManager.updateDefaultShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customer.updateShippingDefaultAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerManager.updateDefaultShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customer.updateShippingDefaultAddress(command.addressId)
        }
    }
}