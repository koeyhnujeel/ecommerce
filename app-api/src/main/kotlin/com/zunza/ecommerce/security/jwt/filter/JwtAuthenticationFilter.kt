package com.zunza.ecommerce.security.jwt.filter

import com.zunza.ecommerce.port.TokenProvider
import com.zunza.ecommerce.repository.TokenBlacklistRepository
import com.zunza.ecommerce.support.exception.CustomTokenException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val tokenProvider: TokenProvider,
    private val tokenBlacklistRepository: TokenBlacklistRepository
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

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            if (isBlacklisted(token)) {
                throw CustomTokenException("토큰이 만료되었습니다.")
            }

            val authentication = getAuthentication(token)
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

    private fun isBlacklisted(token: String): Boolean {
        val jti = tokenProvider.getJti(token)
        return tokenBlacklistRepository.existsByJti(jti)
    }

    private fun getAuthentication(token: String): Authentication {
        return UsernamePasswordAuthenticationToken(
            tokenProvider.getUserId(token),
            null,
            listOf(SimpleGrantedAuthority("ROLE_${tokenProvider.getUserRole(token)}"))
        )
    }
}
