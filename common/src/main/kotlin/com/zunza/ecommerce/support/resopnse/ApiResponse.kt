package com.zunza.ecommerce.support.resopnse

import com.fasterxml.jackson.annotation.JsonInclude
import com.zunza.ecommerce.support.exception.ErrorResponse

data class ApiResponse<T>(
    val result: ResultType,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val error: ErrorResponse? = null,
) {
    companion object {
        fun success(): ApiResponse<Any> = ApiResponse(ResultType.SUCCESS, null, null)

        fun <S> success(data: S): ApiResponse<S> = ApiResponse(ResultType.SUCCESS, data, null)

        fun error(error: ErrorResponse): ApiResponse<Any> = ApiResponse(ResultType.ERROR, null, error)
    }
}
