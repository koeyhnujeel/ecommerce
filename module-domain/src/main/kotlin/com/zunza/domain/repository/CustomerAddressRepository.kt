package com.zunza.domain.repository

import com.zunza.domain.entity.CustomerAddress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerAddressRepository : JpaRepository<CustomerAddress, Long> {
    fun findByCustomerId(customer: Long): CustomerAddress?
}
