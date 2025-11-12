package com.zunza.domain.entity

import com.zunza.domain.enums.PartnerStatus
import com.zunza.domain.enums.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "partners")
class Partner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val email: String = "",
    @Column(nullable = false, unique = true)
    val businessName: String = "",
    @Column(nullable = false, unique = true)
    val businessNumber: String = "",
    @Column(nullable = false)
    val representativeName: String = "",
    @Column(nullable = false, unique = true)
    val phone: String = "",
    @Column(nullable = false)
    val status: PartnerStatus = PartnerStatus.PENDING,
    @Column(nullable = false)
    val userRole: UserRole = UserRole.ROLE_PARTNER,
) : BaseTimeEntity()
