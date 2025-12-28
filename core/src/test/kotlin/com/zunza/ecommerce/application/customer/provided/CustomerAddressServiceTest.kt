package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerAddressService
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteAddressCommand
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultAddressCommand
import com.zunza.ecommerce.application.fixture.AddressCommandFixture
import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CustomerAddressServiceTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var customerAddressService: CustomerAddressService

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        customerAddressService = CustomerAddressService(customerRepository)
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

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        val result = customerAddressService.registerAddress(command)

        result shouldBe accountId

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.registerAddress(
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

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerAddressService.registerAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.registerAddress(
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
    fun updateAddress() {
        val accountId = 1L;
        val command = AddressCommandFixture.createUpdateAddressCommand(accountId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        customerAddressService.updateAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.updateAddress(
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
            customerAddressService.updateAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.updateAddress(
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
        val command = DeleteAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        customerAddressService.deleteAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.deleteAddress(command.addressId)
        }
    }

    @Test
    fun deleteAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerAddressService.deleteAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.deleteAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        customerAddressService.updateDefaultAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.updateDefaultAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            customerAddressService.updateDefaultAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.updateDefaultAddress(command.addressId)
        }
    }
}