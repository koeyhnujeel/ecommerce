package com.zunza.ecommerce.adapter.persistence.partner

import com.zunza.ecommerce.domain.partner.Partner
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerJpaRepository : JpaRepository<Partner, Long> {
}