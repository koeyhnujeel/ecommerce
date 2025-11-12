package com.zunza.domain.repository

import com.zunza.domain.entity.Brand
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BrandRepository : JpaRepository<Brand, Long> {
    fun existsByIdAndPartnerId(
        brandId: Long,
        partnerId: Long,
    ): Boolean
}
