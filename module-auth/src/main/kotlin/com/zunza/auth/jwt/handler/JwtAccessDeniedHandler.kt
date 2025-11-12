package com.zunza.auth.jwt.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.common.support.exception.ErrorResponse
import com.zunza.common.support.resopnse.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import java.nio.charset.StandardCharsets

class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?,
    ) {
        response!!.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val errorResponse = ErrorResponse(message = "접근 권한이 없습니다.")
        val apiResponse = ApiResponse.error(errorResponse)

        objectMapper.writeValue(response.writer, apiResponse)
    }
}
