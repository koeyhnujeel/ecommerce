package com.zunza.ecommerce.adapter.persistence.partner

import com.zunza.ecommerce.application.partner.required.PartnerRepository
import com.zunza.ecommerce.domain.partner.Partner
import org.springframework.stereotype.Repository

@Repository
class PartnerRepositoryImpl(
    private val partnerJpaRepository: PartnerJpaRepository
) : PartnerRepository {
    override fun save(partner: Partner): Partner {
        return partnerJpaRepository.save(partner)
    }
}