package com.zunza.apis.auth.jwt.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.apis.support.exception.ErrorResponse
import com.zunza.apis.support.resopnse.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import java.nio.charset.StandardCharsets

class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?,
    ) {
        response!!.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val errorResponse = ErrorResponse(message = "로그인이 필요한 작업입니다.")
        val apiResponse = ApiResponse.error(errorResponse)

        objectMapper.writeValue(response.writer, apiResponse)
    }
}
