package com.zunza.auth.jwt.filter

import com.zunza.auth.jwt.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {
    companion object {
        private const val BEARER = "Bearer "
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val REFRESH_URI = "/api/auth/token/refresh"
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        println(request.requestURI)
        return request.requestURI == REFRESH_URI
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authorizationHeader = request.getHeader(AUTHORIZATION_HEADER)
        val token = extractToken(authorizationHeader)

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            val authentication = jwtProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(header: String?): String {
        if (!header.isNullOrBlank() && header.startsWith(BEARER)) {
            return header.substring(BEARER.length)
        }

        return ""
    }
}
