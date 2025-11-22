package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.PartnerProfileEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PartnerProfileJpaRepository : JpaRepository<PartnerProfileEntity, Long>
