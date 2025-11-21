package com.zunza.ecommerce.security.jwt.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.ecommerce.support.exception.CustomTokenException
import com.zunza.ecommerce.support.exception.ErrorResponse
import com.zunza.ecommerce.support.resopnse.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

class JwtExceptionFilter(
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: CustomTokenException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON.toString()
            response.characterEncoding = StandardCharsets.UTF_8.name()

            val errorResponse = ErrorResponse(message = e.message)
            val apiResponse = ApiResponse.error(errorResponse)

            objectMapper.writeValue(response.writer, apiResponse)
        }
    }
}
