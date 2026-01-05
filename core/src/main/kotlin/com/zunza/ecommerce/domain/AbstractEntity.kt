package com.zunza.ecommerce.domain

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.AbstractAggregateRoot

@MappedSuperclass
abstract class AbstractEntity<T : AbstractEntity<T>>(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
) : AbstractAggregateRoot<T>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false

        val thisEffectiveClass = if (this is HibernateProxy)
            this.hibernateLazyInitializer.persistentClass
        else
            this::class.java

        val otherEffectiveClass = if (other is HibernateProxy)
            other.hibernateLazyInitializer.persistentClass
        else
            other::class.java

        if (thisEffectiveClass != otherEffectiveClass) return false

        other as AbstractEntity<T>
        return this.id != 0L && this.id == other.id
    }

    override fun hashCode(): Int {
        return if (this is HibernateProxy)
            this.hibernateLazyInitializer.persistentClass.hashCode()
        else
            this::class.java.hashCode()
    }
}