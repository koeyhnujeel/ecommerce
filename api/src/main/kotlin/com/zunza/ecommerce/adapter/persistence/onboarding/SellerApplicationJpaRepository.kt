package com.zunza.ecommerce.adapter.persistence.onboarding

import com.zunza.ecommerce.domain.onboarding.SellerApplication
import org.springframework.data.jpa.repository.JpaRepository

interface SellerApplicationJpaRepository : JpaRepository<SellerApplication, Long> {
    fun existsByBusinessInfoBusinessNumber(businessNumber: String): Boolean

    fun findByAccountId(accountId: Long): SellerApplication?
}