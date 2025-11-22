package com.zunza.ecommerce.persistence.entity

import com.zunza.ecommerce.domain.enums.PartnerStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "partners")
class PartnerProfileEntity(
    @Id
    val id: Long = 0,
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val userEntity: UserEntity,
    @Column(nullable = false)
    val representativeName: String,
    @Column(unique = true, nullable = false)
    val businessName: String,
    @Column(unique = true, nullable = false)
    val businessNumber: String,
    @Column(nullable = false)
    val settlementAccountBank: String,
    @Column(unique = true, nullable = false)
    val settlementAccountNumber: String,
    @Column(unique = true, nullable = false)
    val phone: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: PartnerStatus = PartnerStatus.PENDING,
)
