package com.zunza.domain.repository.extensions

import com.zunza.common.support.exception.ErrorCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

inline fun <reified T, ID : Any> JpaRepository<T, ID>.findByIdOrThrow(
    id: ID,
    entityName: String = T::class.java.simpleName
): T {
    return this.findByIdOrNull(id)
        ?: throw ErrorCode.NOT_FOUND.exception("$entityName 정보를 찾을 수 없습니다.")
}
