package com.zunza.ecommerce.persistence.jpa

import com.zunza.ecommerce.persistence.entity.CustomerProfileEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CustomerProfileJpaRepository : JpaRepository<CustomerProfileEntity, Long> {
    fun existsByNickname(nickname: String): Boolean

    fun existsByPhone(phone: String): Boolean

    @Query(
        """
            SELECT cp
            FROM CustomerProfileEntity cp
            JOIN FETCH cp.userEntity u
            WHERE cp.id = :id
        """
    )
    fun findWithUserById(@Param("id") id: Long): CustomerProfileEntity?
}
