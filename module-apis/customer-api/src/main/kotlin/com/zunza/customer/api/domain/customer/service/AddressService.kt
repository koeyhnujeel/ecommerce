package com.zunza.customer.api.domain.customer.service

import com.zunza.customer.api.domain.customer.dto.request.AddressRegisterRequestDto
import com.zunza.domain.repository.CustomerAddressRepository
import com.zunza.domain.repository.CustomerRepository
import com.zunza.domain.repository.extensions.findByIdOrThrow
import org.springframework.stereotype.Service

@Service
class AddressService(
    private val customerRepository: CustomerRepository,
    private val customerAddressRepository: CustomerAddressRepository,
) {
    fun registerAddress(
        customerId: Long,
        request: AddressRegisterRequestDto,
    ) {
        val customer = customerRepository.findByIdOrThrow(customerId)
        val customerAddress = request.toEntity(customer)
        customerAddressRepository.save(customerAddress)
    }
}
