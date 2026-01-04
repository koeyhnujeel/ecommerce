package com.zunza.ecommerce.application.partner.required

import com.zunza.ecommerce.domain.partner.Partner

interface PartnerRepository {
    fun save(partner: Partner): Partner
}