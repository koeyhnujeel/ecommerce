package com.zunza.customer.api.domain.customer.service

import com.zunza.apis.support.exception.ErrorCode
import com.zunza.customer.api.domain.customer.dto.request.AddressRegisterRequestDto
import com.zunza.domain.repository.CustomerAddressRepository
import com.zunza.domain.repository.CustomerRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class AddressService(
    private val customerRepository: CustomerRepository,
    private val customerAddressRepository: CustomerAddressRepository
) {
    fun registerAddress(
        customerId: Long,
        request: AddressRegisterRequestDto
    ) {
        val customer = customerRepository.findByIdOrNull(customerId)
            ?: throw ErrorCode.NOT_FOUND.exception("사용자를 찾을 수 없습니다.")

        val customerAddress = request.toEntity(customer)
        customerAddressRepository.save(customerAddress)
    }
}
