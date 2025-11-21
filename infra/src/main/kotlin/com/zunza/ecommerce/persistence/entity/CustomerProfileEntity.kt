package com.zunza.ecommerce.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "customer_profiles")
class CustomerProfileEntity(
    @Id
    val id: Long = 0,
    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    val userEntity: UserEntity,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false)
    val nickname: String,
    @Column(nullable = false)
    val phone: String,
    @Column(nullable = false)
    var point: Long
)
