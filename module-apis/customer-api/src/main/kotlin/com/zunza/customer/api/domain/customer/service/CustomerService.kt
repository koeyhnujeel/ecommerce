package com.zunza.customer.api.domain.customer.service

import com.zunza.apis.support.exception.ErrorCode
import com.zunza.customer.api.domain.customer.dto.request.SignupRequestDto
import com.zunza.customer.api.domain.customer.util.generateRandomNickname
import com.zunza.domain.entity.Customer
import com.zunza.domain.repository.CustomerRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun isEmailAvailable(email: String) {
        if (customerRepository.existsByEmail(email)) {
            throw ErrorCode.CONFLICT.exception("이미 사용 중인 이메일입니다.")
        }
    }

    fun isPhoneAvailable(phone: String) {
        if (customerRepository.existsByPhone(phone)) {
            throw ErrorCode.CONFLICT.exception("이미 등록된 번호입니다.")
        }
    }

    fun signup(request: SignupRequestDto) {
        val encodedPassword = passwordEncoder.encode(request.password)
        val randomNickname = getRandomNickname()

        val customer =
            Customer.of(
                request.email,
                encodedPassword,
                request.name,
                randomNickname,
                request.phone,
            )

        customerRepository.save(customer)
    }

    private fun getRandomNickname() =
        generateSequence { generateRandomNickname() }
            .first { !customerRepository.existsByNickname(it) }
}
