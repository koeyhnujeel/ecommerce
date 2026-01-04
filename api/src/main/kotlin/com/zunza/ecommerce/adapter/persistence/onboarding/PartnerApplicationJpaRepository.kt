package com.zunza.ecommerce.adapter.persistence.onboarding

import com.zunza.ecommerce.domain.onboarding.PartnerApplication
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerApplicationJpaRepository : JpaRepository<PartnerApplication, Long> {
    fun existsByBusinessInfoBusinessNumber(businessNumber: String): Boolean

    fun findByAccountId(accountId: Long): PartnerApplication?
}